package edu.uta.groceryplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uta.groceryplanner.beans.FriendsBean;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference friendsRef;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.content,new ListFragment()).commit();
                    setTitle("List");
                    return true;
                case R.id.navigation_statistics:
                    fragmentTransaction.replace(R.id.content,new StatisticsFragment()).commit();
                    setTitle("Statistics");
                    return true;
                case R.id.navigation_expense_splitter:
                    fragmentTransaction.replace(R.id.content,new ExpenseSplitterFragment()).commit();
                    setTitle("Expense Splitter");
                    return true;
                case R.id.navigation_info:
                    fragmentTransaction.replace(R.id.content,new InfoFragment()).commit();
                    setTitle("Info");
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        friendsRef= FirebaseDatabase.getInstance().getReference("friends").child(firebaseAuth.getCurrentUser().getUid());
        friendsRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                    friendsRef.child(firebaseAuth.getCurrentUser().getUid()).setValue(new FriendsBean(firebaseAuth.getCurrentUser().getUid(), firebaseAuth.getCurrentUser().getDisplayName(), firebaseAuth.getCurrentUser().getEmail(), "resolved", 0));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content,new ListFragment()).commit();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
