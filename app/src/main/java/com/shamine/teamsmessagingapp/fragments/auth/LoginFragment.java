package com.shamine.teamsmessagingapp.fragments.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shamine.teamsmessagingapp.MainActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.databinding.FragmentLoginBinding;
import com.shamine.teamsmessagingapp.room.repositories.UserRepository;
import com.shamine.teamsmessagingapp.utils.Utils;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) requireActivity();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmailAddress.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (!Utils.stringNullOrEmpty(email) && !Utils.stringNullOrEmpty(password)) {
                if (!Utils.isValidEmailAddress(email)) {
                    Toast.makeText(requireContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                } else {
                    mainActivity.displayDotsDialog("Logging in");
                    UserRepository repository = new UserRepository(mainActivity.getApplication());
                    repository.login(mainActivity, email, password);
                }
            } else {
                Toast.makeText(requireContext(), "Fill in all the required details", Toast.LENGTH_SHORT).show();
            }
        });

        binding.txtRegister.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.actionLoginToRegister));

        binding.txtForgotPassword.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.actionLoginToForgotPassword));
    }
}
