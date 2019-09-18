package senac.ensineme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import senac.ensineme.providers.DemandaSuggestionProvider;

public class SettingsActivityProf extends AppCompatActivity {

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
        private Preference nome;
        private Preference email;



        @Override

        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            bindView();
            listener();

        }

        private void bindView(){

            nome = findPreference(getString(R.string.nome));
            email = findPreference(getString(R.string.email));
            notificacao = findPreference(getString(R.string.notification));
            autenticacao = findPreference(getString(R.string.credencials));
            historico = findPreference(getString(R.string.historic));

            nome.setSummary(String.valueOf(ProfessorMainActivity.nomeUsuario));
            email.setSummary(String.valueOf(ProfessorMainActivity.emailUsuario));

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
/*
            autenticacao.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(!((SwitchPreference) preference).isChecked()){

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                                getResources().getString(R.string.pref_data),MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(getResources().getString(R.string.pref_Nome), nome);
                        editor.putString(getResources().getString(R.string.pref_Email), email);
                        editor.commit();
                    }
                    else{


                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(

                                getResources().getString(R.string.pref_data),MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(getResources().getString(R.string.pref_Nome),"");

                        editor.putString(getResources().getString(R.string.pref_Email),"");

                        editor.commit();



                    }

                    return true;

                }

            });

*/

            historico.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override

                public boolean onPreferenceClick(Preference preference) {
                    String key = preference.getKey();
                    if(key.equalsIgnoreCase(getResources().getString(R.string.historic))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Deseja excluir o histórico de busca?")
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                                                DemandaSuggestionProvider.AUTHORITY, DemandaSuggestionProvider.MODE);
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