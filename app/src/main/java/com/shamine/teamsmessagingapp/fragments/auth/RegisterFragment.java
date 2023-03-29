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
import com.shamine.teamsmessagingapp.databinding.FragmentRegisterBinding;
import com.shamine.teamsmessagingapp.room.repositories.UserRepository;
import com.shamine.teamsmessagingapp.utils.Utils;

public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) requireActivity();
        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            String username = binding.edtUsername.getText().toString().trim();
            String email = binding.edtEmailAddress.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();

            if (!Utils.stringNullOrEmpty(name) && !Utils.stringNullOrEmpty(username) &&
                    !Utils.stringNullOrEmpty(email) && !Utils.stringNullOrEmpty(password) &&
                    !Utils.stringNullOrEmpty(confirmPassword)) {
                if (!Utils.isValidName(name)) {
                    Toast.makeText(requireContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();
                }
                if (!Utils.isValidUsername(username)) {
                    Toast.makeText(requireContext(), "Enter a valid username", Toast.LENGTH_SHORT).show();
                } else if (!Utils.isValidEmailAddress(email)) {
                    Toast.makeText(requireContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(requireContext(), "The passwords entered should match", Toast.LENGTH_SHORT).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(OTPFragment.NAME_KEY, name);
                    bundle.putString(OTPFragment.USERNAME_KEY, username);
                    bundle.putString(OTPFragment.EMAIL_KEY, email);
                    bundle.putString(OTPFragment.PASSWORD_KEY, password);

                    mainActivity.displayDotsDialog("Sending OTP");
                    UserRepository repository = new UserRepository(mainActivity.getApplication());
                    repository.requestOTP(mainActivity, email, bundle, true);
                }
            } else {
                Toast.makeText(requireContext(), "Fill in all the required details", Toast.LENGTH_SHORT).show();
            }
        });
        binding.txtLogin.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.actionRegisterBackToLogin));
        binding.txtForgotPassword.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.actionRegisterToForgotPassword));
    }
}
