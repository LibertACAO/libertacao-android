package com.libertacao.libertacao.view.perfil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.libertacao.libertacao.R;
import com.libertacao.libertacao.util.ViewUtils;
import com.parse.ParseUser;

import butterknife.ButterKnife;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        ViewUtils.setHomeAsUpEnabled(this);
        ButterKnife.inject(this);
        ParseUser currentUser = ParseUser.getCurrentUser();
    }
}
