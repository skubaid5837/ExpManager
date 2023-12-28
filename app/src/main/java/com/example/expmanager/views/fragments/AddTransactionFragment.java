package com.example.expmanager.views.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.expmanager.R;
import com.example.expmanager.adapters.AccountsAdapter;
import com.example.expmanager.adapters.CategoryAdapter;
import com.example.expmanager.databinding.FragmentAddTransactionBinding;
import com.example.expmanager.databinding.ListDialogBinding;
import com.example.expmanager.models.Account;
import com.example.expmanager.models.Category;
import com.example.expmanager.models.Transaction;
import com.example.expmanager.utils.Constants;
import com.example.expmanager.utils.Helper;
import com.example.expmanager.views.activities.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddTransactionFragment extends BottomSheetDialogFragment {

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentAddTransactionBinding binding;
    Transaction transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTransactionBinding.inflate(inflater);

        transaction = new Transaction();

        binding.incomeBtn.setOnClickListener(v -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.income_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.textColor));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.green));

            transaction.setType(Constants.INCOME);
        });

        binding.expenseBtn.setOnClickListener(v -> {
            binding.incomeBtn.setBackground(getContext().getDrawable(R.drawable.default_selector));
            binding.expenseBtn.setBackground(getContext().getDrawable(R.drawable.expense_selector));
            binding.expenseBtn.setTextColor(getContext().getColor(R.color.textColor));
            binding.incomeBtn.setTextColor(getContext().getColor(R.color.red));

            transaction.setType(Constants.EXPENSE);
        });

        binding.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext());
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
                        calendar.set(Calendar.MONTH, view.getMonth());
                        calendar.set(Calendar.YEAR, view.getYear());

                        String dateShow = Helper.formatDate(calendar.getTime());

                        binding.date.setText(dateShow);

                        transaction.setDate(calendar.getTime());
                        transaction.setId(calendar.getTime().getTime());
                    }
                });
                datePickerDialog.show();
            }
        });

        binding.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
                AlertDialog categoryDialog = new AlertDialog.Builder(getContext()).create();
                categoryDialog.setView(dialogBinding.getRoot());

                CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), Constants.categories, new CategoryAdapter.CategoryClickListener() {
                    @Override
                    public void onCategoryClicked(Category category) {
                        binding.category.setText(category.getCategoryName());
                        transaction.setCategory(category.getCategoryName());
                        categoryDialog.dismiss();
                    }
                });
                dialogBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
                dialogBinding.recyclerView.setAdapter(categoryAdapter);
                categoryDialog.show();
            }
        });

        binding.account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListDialogBinding dialogBinding = ListDialogBinding.inflate(inflater);
                AlertDialog accountsDialog = new AlertDialog.Builder(getContext()).create();
                accountsDialog.setView(dialogBinding.getRoot());

                ArrayList<Account> accounts = new ArrayList<>();
                accounts.add(new Account(0, "Cash"));
                accounts.add(new Account(0, "Card"));
                accounts.add(new Account(0, "Bank"));
                accounts.add(new Account(0, "Upi"));

                AccountsAdapter accountsAdapter = new AccountsAdapter(getContext(), accounts, new AccountsAdapter.AccountsClickListener() {
                    @Override
                    public void onAccountSelected(Account account) {
                        binding.account.setText(account.getAccount_name());
                        transaction.setAccount(account.getAccount_name());
                        accountsDialog.dismiss();
                    }
                });

                dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                dialogBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                dialogBinding.recyclerView.setAdapter(accountsAdapter);
                accountsDialog.show();
            }
        });

        binding.saveTransactionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double amount = Double.parseDouble(binding.amount.getText().toString());
                String note = binding.note.getText().toString();

                if(transaction.getType().equals(Constants.EXPENSE)){
                    transaction.setAmount(amount*-1);
                }else {
                    transaction.setAmount(amount);
                }
                transaction.setNote(note);

                ((MainActivity)getActivity()).viewModel.addTransactions(transaction);
                ((MainActivity)getActivity()).getTransactions();
                dismiss();
            }
        });

        return binding.getRoot();
    }
}