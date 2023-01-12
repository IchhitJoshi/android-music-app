package com.example.demo.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.demo.R;
import com.google.firebase.auth.FirebaseAuth;

import com.example.demo.views.Login.MainActivity;


import utils.SharedPref;

import static com.example.demo.views.Home.nightMode;
import static com.example.demo.views.Home.systemDefault;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    FirebaseAuth fAuth;
    SharedPref sharedPref;



    public HomeFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.app_color_mode, menu);
        super.onCreateOptionsMenu(menu, inflater);


        if (sharedPref.loadSystemDefaultState()){
            menu.getItem(2).setChecked(true);
        }else{
            if(sharedPref.loadDarkModeState()){
                menu.getItem(1).setChecked(true);
            }else{
                menu.getItem(0).setChecked(true);
            }
        }




    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.light_mode){

            item.setChecked(true);

            if (sharedPref.loadDarkModeState()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPref.saveSystemDefault(false);
                sharedPref.saveDarkMode(false);
            }




        }
        if (item.getItemId() == R.id.dark_mode){

            item.setChecked(true);

            if (!sharedPref.loadDarkModeState()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                nightMode = true;
                systemDefault = false;
                sharedPref.saveSystemDefault(false);
                sharedPref.saveDarkMode(true);

            }


        }

        if (item.getItemId() == R.id.system_default){
            item.setChecked(true);

            if (!sharedPref.loadSystemDefaultState()){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                systemDefault = true;
                sharedPref.saveSystemDefault(true);

                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    nightMode = true;
                    sharedPref.saveDarkMode(true);
                }else{
                    nightMode = false;
                    sharedPref.saveDarkMode(false);
                }
            }


        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbarHome);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        sharedPref = ((Home) (getActivity())).getSharedPref();

        if(nightMode){
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.overflow_white));
        }else{
            toolbar.setOverflowIcon(ContextCompat.getDrawable(this.getContext(), R.drawable.overflow_gray));
        }

        Button logOutButton = (Button) view.findViewById(R.id.logOutButton);
        fAuth = FirebaseAuth.getInstance();

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

            }
        });


        return view;


    }





}


