package senac.ensineme.providers;

import android.content.SearchRecentSuggestionsProvider;

public class DemandaSuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "senac.ensineme.providers.DemandaSuggestionProvider";

    public final static int MODE = DATABASE_MODE_QUERIES;



    public DemandaSuggestionProvider() {

        setupSuggestions(AUTHORITY, MODE);

    }

}