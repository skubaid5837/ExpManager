package com.example.expmanager.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expmanager.R;
import com.example.expmanager.adapters.TransactionsAdapter;
import com.example.expmanager.databinding.FragmentTransactionsBinding;
import com.example.expmanager.models.Transaction;
import com.example.expmanager.utils.Constants;
import com.example.expmanager.utils.Helper;
import com.example.expmanager.viewmodels.MainViewModel;
import com.example.expmanager.views.activities.MainActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import io.realm.RealmResults;

public class TransactionsFragment extends Fragment {


    public TransactionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentTransactionsBinding binding;

    Calendar calendar;
    public MainViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        calendar = Calendar.getInstance();
        updateDate();

        binding.nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.SELECTED_TAB == Constants.DAILY){
                    calendar.add(Calendar.DATE, 1);
                }else {
                    calendar.add(Calendar.MONTH, 1);
                }

                updateDate();
            }
        });

        binding.previousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.SELECTED_TAB == Constants.DAILY) {
                    calendar.add(Calendar.DATE, -1);
                }else {
                    calendar.add(Calendar.MONTH, -1);
                }
                updateDate();
            }
        });

        binding.floatingActionButton.setOnClickListener(c -> {
            new AddTransactionFragment().show(getParentFragmentManager(),null);
        });

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Monthly")) {
                    Constants.SELECTED_TAB = Constants.MONTHLY;
                    updateDate();
                } else if (tab.getText().equals("Daily")) {
                    Constants.SELECTED_TAB = Constants.DAILY;
                    updateDate();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.transactionList.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.transactions.observe(getViewLifecycleOwner(), new Observer<RealmResults<Transaction>>() {
            @Override
            public void onChanged(RealmResults<Transaction> transactions) {

                TransactionsAdapter transactionsAdapter = new TransactionsAdapter(getActivity(), transactions);
                binding.transactionList.setAdapter(transactionsAdapter);

              if(transactions.size() > 0) {
                   binding.emptyState.setVisibility(View.GONE);
              }else {
                    binding.emptyState.setVisibility(View.VISIBLE);
              }
            }
        });

        viewModel.totalIncome.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.incomeLb.setText(String.valueOf(aDouble));
            }
        });

        viewModel.totalExpense.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.expenseLb.setText(String.valueOf(aDouble));
            }
        });

        viewModel.totalAmount.observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                binding.totalLb.setText(String.valueOf(aDouble));
            }
        });

        viewModel.getTransaction(calendar);

        return binding.getRoot();
    }

    void updateDate() {
        if(Constants.SELECTED_TAB == Constants.DAILY){
            binding.currentDate.setText(Helper.formatDate(calendar.getTime()));
        }else if(Constants.SELECTED_TAB == Constants.MONTHLY){
            binding.currentDate.setText(Helper.formatDateByMonth(calendar.getTime()));
        }
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, YYYY");
        viewModel.getTransaction(calendar);
    }

}