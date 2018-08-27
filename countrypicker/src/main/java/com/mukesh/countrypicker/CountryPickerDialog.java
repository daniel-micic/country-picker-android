package com.mukesh.countrypicker;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryPickerDialog extends DialogFragment implements OnItemClickListener {

  // region Variables
  private CountryPickerDialogInteractionListener dialogInteractionListener;
  private EditText searchEditText;
  private RecyclerView countriesRecyclerView;
  private CountriesAdapter adapter;
  private List<Country> searchResults;
  private OnCountryPickerListener listener;
  // endregion

  // region Constructors
  public static CountryPickerDialog newInstance() {
    return new CountryPickerDialog();
  }
  // endregion

  // region Lifecycle
  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.country_picker, null);
    getDialog().setTitle(R.string.country_picker_header);
    searchEditText = view.findViewById(R.id.country_code_picker_search);
    countriesRecyclerView = view.findViewById(R.id.countries_recycler_view);
    setupRecyclerView();
    if (!dialogInteractionListener.canSearch()) {
      searchEditText.setVisibility(View.GONE);
    }
    searchEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Intentionally Empty
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Intentionally Empty
      }

      @Override
      public void afterTextChanged(Editable searchQuery) {
        search(searchQuery.toString());
      }
    });
    searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        InputMethodManager imm = (InputMethodManager) searchEditText.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
          imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        }
        return true;
      }
    });
    return view;
  }

  @Override public void onStart() {
    super.onStart();
    ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
    params.height = LinearLayout.LayoutParams.MATCH_PARENT;
    getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
  }

  @Override public void onItemClicked(Country country) {
    if (listener != null) {
      listener.onSelectCountry(country);
    }
    dismiss();
  }
  // endregion

  // region Setter Methods
  public void setCountryPickerListener(OnCountryPickerListener listener) {
    this.listener = listener;
  }

  public void setDialogInteractionListener(
      CountryPickerDialogInteractionListener dialogInteractionListener) {
    this.dialogInteractionListener = dialogInteractionListener;
  }
  // endregion

  // region Utility Methods
  private void search(String searchQuery) {
    searchResults.clear();
    for (Country country : dialogInteractionListener.getAllCountries()) {
      if (country.getName().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
        searchResults.add(country);
      }
    }
    dialogInteractionListener.sortCountries(searchResults);
    adapter.notifyDataSetChanged();
  }

  private void setupRecyclerView() {
    searchResults = new ArrayList<>();
    searchResults.addAll(dialogInteractionListener.getAllCountries());
    adapter = new CountriesAdapter(getActivity(), searchResults, this);
    countriesRecyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    countriesRecyclerView.setLayoutManager(layoutManager);
    countriesRecyclerView.setAdapter(adapter);
  }

  // endregion

  //region Interface
  public interface CountryPickerDialogInteractionListener {
    List<Country> getAllCountries();

    void sortCountries(List<Country> searchResults);

    boolean canSearch();
  }
  // endregion
}
