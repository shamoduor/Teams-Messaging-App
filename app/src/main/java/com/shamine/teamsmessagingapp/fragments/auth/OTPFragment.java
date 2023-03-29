package com.shamine.teamsmessagingapp.fragments.auth;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.shamine.teamsmessagingapp.MainActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.databinding.FragmentOtpBinding;
import com.shamine.teamsmessagingapp.room.repositories.UserRepository;
import com.shamine.teamsmessagingapp.utils.Utils;

public class OTPFragment extends Fragment {
    private FragmentOtpBinding binding;

    static final String NAME_KEY = "NAME_KEY";
    static final String USERNAME_KEY = "USERNAME_KEY";
    static final String EMAIL_KEY = "EMAIL_KEY";
    static final String PASSWORD_KEY = "PASSWORD_KEY";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();

        MainActivity mainActivity = (MainActivity) requireActivity();
        if (bundle != null && bundle.containsKey(NAME_KEY) && bundle.containsKey(USERNAME_KEY) && bundle.containsKey(EMAIL_KEY) && bundle.containsKey(PASSWORD_KEY)) {

            startCountDownTimer();

            String emailAddress = bundle.getString(EMAIL_KEY, "");
            String emailMsg = "Sent to (" + emailAddress + ")";
            binding.txtSubHeader.setText(emailMsg);

            binding.btnVerifyAndProceed.setOnClickListener(v -> {
                String gottenOTPText = binding.edtOTP.getText().toString();

                if (Utils.stringNullOrEmpty(gottenOTPText)) {
                    Toast.makeText(mainActivity, "Kindly enter the complete OTP", Toast.LENGTH_LONG).show();
                } else {
                    int gottenOTP = Integer.parseInt(binding.edtOTP.getText().toString());

                    if (gottenOTP >= 100000 && gottenOTP <= 999999) {
                        String gottenName = bundle.getString(NAME_KEY, "");
                        String gottenUsername = bundle.getString(USERNAME_KEY, "");
                        String gottenPassword = bundle.getString(PASSWORD_KEY, "");

                        mainActivity.displayDotsDialog("Registering user");
                        UserRepository repository = new UserRepository(mainActivity.getApplication());
                        repository.register(mainActivity, gottenName, gottenUsername, emailAddress, gottenPassword, gottenOTP);
                    } else {
                        Toast.makeText(mainActivity, "Kindly enter a valid OTP", Toast.LENGTH_LONG).show();
                    }
                }
            });

            binding.btnResendSms.setOnClickListener(v -> {
                if (Utils.isValidEmailAddress(emailAddress)) {
                    UserRepository repo = new UserRepository(mainActivity.getApplication());
                    repo.requestOTP(mainActivity, emailAddress, null, false);
                    startCountDownTimer();
                }
            });
        } else {
            mainActivity.getOnBackPressedDispatcher().onBackPressed();
        }
    }

    private final CountDownTimer resendSmsTimer = new CountDownTimer(120000, 250) {
        @Override
        public void onTick(long millisUntilFinished) {
            String timeRemaining = "Resend SMS " + "(" + Utils.getFormattedTime(millisUntilFinished) + ")";
            binding.txtResendTimer.setText(timeRemaining);
        }

        @Override
        public void onFinish() {
            binding.txtResendTimer.setVisibility(View.GONE);
            binding.btnResendSms.setVisibility(View.VISIBLE);
        }
    };

    private void startCountDownTimer() {
        binding.txtResendTimer.setVisibility(View.VISIBLE);
        binding.btnResendSms.setVisibility(View.GONE);
        resendSmsTimer.start();
    }
}
