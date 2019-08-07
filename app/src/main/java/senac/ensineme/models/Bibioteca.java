package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Bibioteca {
    private static DatabaseReference firebase;

    private Bibioteca(){}

    public static DatabaseReference getFirebase(){
        if( firebase == null ){
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return( firebase );
    }
}
