package com.zidi.flowidentification_demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zidi.flowidentification_demo.model.LoginRequest;
import com.zidi.flowidentification_demo.model.LoginResponse;
import com.zidi.flowidentification_demo.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button loginBtn, logoutBtn, goToUploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 绑定 UI 元素
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        loginBtn = findViewById(R.id.btn_login);
        logoutBtn = findViewById(R.id.btn_logout);
        goToUploadBtn = findViewById(R.id.btn_goto_upload);

        // 登录按钮逻辑
        loginBtn.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            RetrofitClient.getInstance().getAuthApi().login(request).enqueue(new Callback<LoginResponse>(){
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    LoginResponse loginResponse = response.body();

                    if (response.isSuccessful() && loginResponse != null && "success".equals(loginResponse.getStatus())) {
                        Toast.makeText(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        // 登录成功 -> 跳转到 DashboardActivity
                        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        String msg = loginResponse != null ? loginResponse.getMessage() : "Unexpected error";
                        Toast.makeText(MainActivity.this, "Login failed: " + msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // 登出逻辑
        logoutBtn.setOnClickListener(v -> {
            loginBtn.setEnabled(true);
            logoutBtn.setEnabled(false);
            goToUploadBtn.setEnabled(false);
            inputEmail.setText("");
            inputPassword.setText("");
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        });

        // 跳转上传页逻辑
        goToUploadBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        // 初始状态
        logoutBtn.setEnabled(false);
        goToUploadBtn.setEnabled(false);
    }
}
