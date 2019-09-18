package senac.ensineme;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivityAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private Preference notificacao;

        private Preference autenticacao;

        private Preference historico;

        private Preference usuario;



        @Override

        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.preferences, rootKey);



            bindView();

            listener();



        }



        private void bindView(){

            usuario = findPreference(getString(R.string.login));

            notificacao = findPreference(getString(R.string.notification));

            autenticacao = findPreference(getString(R.string.credencials));

            historico = findPreference(getString(R.string.historic));



            usuario.setSummary(login);

        }



        private void listener(){



            notificacao.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override

                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if(!((SwitchPreference) preference).isChecked()){

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(

                                getResources().getString(R.string.pref_data),MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putBoolean(getResources().getString(R.string.pref_notification),true);

                        editor.commit();

                    } else {

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(

                                getResources().getString(R.string.pref_data),MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putBoolean(getResources().getString(R.string.pref_notification),false);

                        editor.commit();

                    }

                    return true;

                }

            });



            autenticacao.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override

                public boolean onPreferenceChange(Preference preference, Object newValue) {

                    if(!((SwitchPreference) preference).isChecked()){



                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(

                                getResources().getString(R.string.pref_data),MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(getResources().getString(R.string.pref_login),login);

                        editor.putString(getResources().getString(R.string.pref_password),password);

                        editor.commit();



                    }

                    else{



                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(

                                getResources().getString(R.string.pref_data),MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(getResources().getString(R.string.pref_login),"");

                        editor.putString(getResources().getString(R.string.pref_password),"");

                        editor.commit();



                    }

                    return true;

                }

            });



            historico.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override

                public boolean onPreferenceClick(Preference preference) {

                    String key = preference.getKey();

                    if(key.equalsIgnoreCase(getResources().getString(R.string.historic))) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);

                        builder.setIcon(android.R.drawable.ic_dialog_alert)

                                .setTitle("REMOVENDO...")

                                .setMessage("Você tem certeza que deseja excluir o histórico de busca?")

                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {

                                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),

                                                SapatoSuggestionProvider.AUTHORITY, SapatoSuggestionProvider.MODE);

                                        suggestions.clearHistory();

                                    }

                                })

                                .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {

                                    @Override

                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.cancel();

                                    }

                                });

                        builder.create();

                        builder.show();

                    }

                    return true;

                }

            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}