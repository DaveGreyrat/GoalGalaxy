package com.example.goalgalaxy.Fragments;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.goalgalaxy.Adapter.ToDoAdapter;
import com.example.goalgalaxy.AddNewTask;
import com.example.goalgalaxy.DateTimePicker;
import com.example.goalgalaxy.DialogCloseListener;
import com.example.goalgalaxy.MainActivity;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.RecyclerItemTouchHelper;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements DialogCloseListener {

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;

    private TextView quoteText;
    private Button changeQuoteButton;

    private ArrayList<String> quotes = new ArrayList<>();
    private Context context;
    private DateTimePicker dateTimePicker;


    public void setContext(Context context) {
        this.context = context;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dateTimePicker = new DateTimePicker();
        dateTimePicker.setContext(requireContext());


        // Initialize TextView and Button
        quoteText = view.findViewById(R.id.quoteText);


        // Add motivational quotes
        addQuotes();

        // Set a random quote initially
        setRandomQuote();



        db = new DatabaseHandler(requireActivity());
        db.openDatabase();

        tasksRecyclerView = view.findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        tasksAdapter = new ToDoAdapter(db, (MainActivity) requireActivity());
        tasksRecyclerView.setAdapter(tasksAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        fab = view.findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tasksAdapter.setTasks(taskList);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTimePicker.clearDateTime();
                AddNewTask.newInstance().show(requireActivity().getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        return view;
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    private void clearRememberMe() {
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("rememberMe");
        editor.apply();
    }

    private void addQuotes() {
        quotes.add("Believe you can and you're halfway there. - Theodore Roosevelt");
        quotes.add("The only way to do great work is to love what you do. - Steve Jobs");
        quotes.add("Success is not final, failure is not fatal: It is the courage to continue that counts. - Winston Churchill");
        quotes.add("Don't watch the clock; do what it does. Keep going. - Sam Levenson");
        quotes.add("It does not matter how slowly you go as long as you do not stop. - Confucius");
        quotes.add("You are never too old to set another goal or to dream a new dream. - C.S. Lewis");
        quotes.add("Our greatest glory is not in never falling, but in rising every time we fall. - Confucius");
        quotes.add("The only person you are destined to become is the person you decide to be. - Ralph Waldo Emerson");
        quotes.add("What you get by achieving your goals is not as important as what you become by achieving your goals. - Zig Ziglar");
        quotes.add("Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle. - Christian D. Larson");
        quotes.add("You miss 100% of the shots you don't take. - Wayne Gretzky");
        quotes.add("The best time to plant a tree was 20 years ago. The second best time is now. - Chinese Proverb");
        quotes.add("Hardships often prepare ordinary people for an extraordinary destiny. - C.S. Lewis");
        quotes.add("The future belongs to those who believe in the beauty of their dreams. - Eleanor Roosevelt");
        quotes.add("Success is not the key to happiness. Happiness is the key to success. If you love what you are doing, you will be successful. - Albert Schweitzer");
        quotes.add("The only limit to our realization of tomorrow will be our doubts of today. - Franklin D. Roosevelt");
        quotes.add("In the middle of difficulty lies opportunity. - Albert Einstein");
        quotes.add("Your limitation—it's only your imagination.");
        quotes.add("Push yourself, because no one else is going to do it for you.");
        quotes.add("Great things never come from comfort zones.");
        quotes.add("Dream it. Wish it. Do it.");
        quotes.add("Success doesn’t just find you. You have to go out and get it.");
        quotes.add("The harder you work for something, the greater you’ll feel when you achieve it.");
        quotes.add("Dream bigger. Do bigger.");
        quotes.add("Don’t stop when you’re tired. Stop when you’re done.");
        quotes.add("Wake up with determination. Go to bed with satisfaction.");
        quotes.add("Do something today that your future self will thank you for.");
        quotes.add("Little things make big days.");
        quotes.add("It’s going to be hard, but hard does not mean impossible.");
        quotes.add("Don’t wait for opportunity. Create it.");
        quotes.add("Sometimes we’re tested not to show our weaknesses, but to discover our strengths.");
        quotes.add("The key to success is to focus on goals, not obstacles.");
        quotes.add("Dream it. Believe it. Build it.");
        quotes.add("Remember why you started.");
        quotes.add("Believe you can and you’re halfway there.");
        quotes.add("Stay positive, work hard, make it happen.");
        quotes.add("You are capable of more than you know.");
        quotes.add("The harder you work, the luckier you get.");
        quotes.add("Push yourself, because no one else is going to do it for you.");
        quotes.add("Great things never come from comfort zones.");
        quotes.add("Dream it. Wish it. Do it.");
        quotes.add("Success doesn’t just find you. You have to go out and get it.");
        quotes.add("The harder you work for something, the greater you’ll feel when you achieve it.");
        quotes.add("Dream bigger. Do bigger.");
        quotes.add("Don’t stop when you’re tired. Stop when you’re done.");
        quotes.add("Wake up with determination. Go to bed with satisfaction.");
        quotes.add("Do something today that your future self will thank you for.");
        quotes.add("Little things make big days.");
        quotes.add("It’s going to be hard, but hard does not mean impossible.");
        quotes.add("Don’t wait for opportunity. Create it.");
        quotes.add("Sometimes we’re tested not to show our weaknesses, but to discover our strengths.");
        quotes.add("The key to success is to focus on goals, not obstacles.");
        quotes.add("Dream it. Believe it. Build it.");
        quotes.add("Remember why you started.");
        quotes.add("Believe you can and you’re halfway there.");
        quotes.add("Stay positive, work hard, make it happen.");
        quotes.add("You are capable of more than you know.");
        quotes.add("The harder you work, the luckier you get.");
    }

    private void setRandomQuote() {
        // Shuffle the quotes
        Collections.shuffle(quotes);
        // Set the first quote (after shuffling)
        quoteText.setText(quotes.get(0));
    }
}
