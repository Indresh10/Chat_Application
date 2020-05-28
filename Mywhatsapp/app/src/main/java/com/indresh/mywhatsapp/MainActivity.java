package com.indresh.mywhatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.indresh.mywhatsapp.Fragment.ChatsFragment;
import com.indresh.mywhatsapp.Fragment.ProfileFragment;
import com.indresh.mywhatsapp.Fragment.UsersFragment;
import com.indresh.mywhatsapp.Model.Chat;
import com.indresh.mywhatsapp.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                assert user != null;
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final TabLayout tabLayout=findViewById(R.id.tab_layout);
        final ViewPager viewPager=findViewById(R.id.view_pager);

        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewpagerAdapter viewpagerAdapter= new ViewpagerAdapter(getSupportFragmentManager());
                int unread=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }
                if (unread==0){
                    viewpagerAdapter.addFragment(new ChatsFragment(),"Chat");
                }
                else {
                    viewpagerAdapter.addFragment(new ChatsFragment(),"("+unread+")Chats");
                }
                viewpagerAdapter.addFragment(new UsersFragment(),"Users");
                viewpagerAdapter.addFragment(new ProfileFragment(),"Profile");
                viewPager.setAdapter(viewpagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
            return true;
        }
        return false;
    }

    static class ViewpagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void Status(String status){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
    }
}
