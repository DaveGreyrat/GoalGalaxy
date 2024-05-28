package com.example.goalgalaxy.Fragments;

import androidx.fragment.app.Fragment;
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
import com.example.goalgalaxy.Tasks.DateTimePicker;
import com.example.goalgalaxy.DialogCloseListener;
import com.example.goalgalaxy.Model.ToDoModel;
import com.example.goalgalaxy.R;
import com.example.goalgalaxy.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Arrays;


public class HomeFragment extends Fragment implements DialogCloseListener {

    private DatabaseHandler db;

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;


    private static TextView quoteText;
    private static TextView authorText;
    private Button changeQuoteButton;

    private static ArrayList<String> quotes = new ArrayList<>();
    private Context context;
    private DateTimePicker dateTimePicker;
    private static Map<String, List<String>> quotesByAuthors = new HashMap<>();


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
        authorText = view.findViewById(R.id.authorText);

        // Заполняем карту цитатами и именами авторов
        fillQuotesByAuthors();

        // Устанавливаем случайную цитату
        setRandomQuote();


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


    private static void fillQuotesByAuthors() {
        // Добавляем цитаты с именами авторов
        quotesByAuthors.put("Steve Jobs", Arrays.asList(
                "The only way to do great work is to love what you do.",
                "Your time is limited, don't waste it living someone else's life."
        ));

        quotesByAuthors.put("Abraham Lincoln", Arrays.asList(
                "In the end, it's not the years in your life that count. It's the life in your years.",
                "Whatever you are, be a good one."
        ));

        quotesByAuthors.put("Theodore Roosevelt", Arrays.asList(
                "Believe you can and you're halfway there."
        ));

        quotesByAuthors.put("Sam Levenson", Arrays.asList(
                "Don't watch the clock; do what it does. Keep going."
        ));

        quotesByAuthors.put("Wayne Gretzky", Arrays.asList(
                "You miss 100% of the shots you don't take."
        ));

        quotesByAuthors.put("John Lennon", Arrays.asList(
                "Life is what happens when you're busy making other plans."
        ));

        quotesByAuthors.put("Eleanor Roosevelt", Arrays.asList(
                "The future belongs to those who believe in the beauty of their dreams."
        ));

        quotesByAuthors.put("Albert Einstein", Arrays.asList(
                "Strive not to be a success, but rather to be of value.",
                "Logic will get you from A to B. Imagination will take you everywhere.",
                "In the middle of difficulty lies opportunity."
        ));

        quotesByAuthors.put("Franklin D. Roosevelt", Arrays.asList(
                "The only limit to our realization of tomorrow will be our doubts of today."
        ));

        quotesByAuthors.put("Chinese Proverb", Arrays.asList(
                "The best time to plant a tree was 20 years ago. The second best time is now."
        ));

        quotesByAuthors.put("Confucius", Arrays.asList(
                "It does not matter how slowly you go as long as you do not stop."
        ));

        quotesByAuthors.put("Nelson Mandela", Arrays.asList(
                "The greatest glory in living lies not in never falling, but in rising every time we fall.",
                "It always seems impossible until it's done."
        ));

        quotesByAuthors.put("Walt Disney", Arrays.asList(
                "The way to get started is to quit talking and begin doing.",
                "All our dreams can come true if we have the courage to pursue them."
        ));

        quotesByAuthors.put("Helen Keller", Arrays.asList(
                "Life is either a daring adventure or nothing at all.",
                "When one door of happiness closes, another opens, but often we look so long at the closed door that we do not see the one that has been opened for us."
        ));

        quotesByAuthors.put("Christopher Columbus", Arrays.asList(
                "You can never cross the ocean until you have the courage to lose sight of the shore."
        ));

        quotesByAuthors.put("Maya Angelou", Arrays.asList(
                "We may encounter many defeats but we must not be defeated.",
                "You can't use up creativity. The more you use, the more you have."
        ));

        quotesByAuthors.put("Ralph Waldo Emerson", Arrays.asList(
                "The only person you are destined to become is the person you decide to be."
        ));

        quotesByAuthors.put("Charles R. Swindoll", Arrays.asList(
                "Life is 10% what happens to us and 90% how we react to it."
        ));

        quotesByAuthors.put("Frank Sinatra", Arrays.asList(
                "The best revenge is massive success."
        ));

        quotesByAuthors.put("Mark Twain", Arrays.asList(
                "The two most important days in your life are the day you are born and the day you find out why.",
                "Twenty years from now you will be more disappointed by the things that you didn't do than by the ones you did do."
        ));

        quotesByAuthors.put("Eric Thomas", Arrays.asList(
                "When you want to succeed as bad as you want to breathe, then you'll be successful."
        ));

        quotesByAuthors.put("John Wooden", Arrays.asList(
                "Do not let what you cannot do interfere with what you can do."
        ));

        quotesByAuthors.put("George Eliot", Arrays.asList(
                "It is never too late to be what you might have been."
        ));

        quotesByAuthors.put("Charles Kingsleigh", Arrays.asList(
                "The only way to achieve the impossible is to believe it is possible."
        ));

        quotesByAuthors.put("Oprah Winfrey", Arrays.asList(
                "You become what you believe."
        ));

        quotesByAuthors.put("Kevin Kruse", Arrays.asList(
                "Life is about making an impact, not making an income."
        ));

        quotesByAuthors.put("Lao Tzu", Arrays.asList(
                "The journey of a thousand miles begins with one step."
        ));

        quotesByAuthors.put("Mark Zuckerberg", Arrays.asList(
                "The biggest risk is not taking any risk. In a world that's changing really quickly, the only strategy that is guaranteed to fail is not taking risks."
        ));

        quotesByAuthors.put("Robert Frost", Arrays.asList(
                "In three words I can sum up everything I've learned about life: It goes on."
        ));

        quotesByAuthors.put("George Bernard Shaw", Arrays.asList(
                "Life is not about finding yourself. Life is about creating yourself."
        ));

        quotesByAuthors.put("Lou Holtz", Arrays.asList(
                "It's not the load that breaks you down, it's the way you carry it."
        ));

        quotesByAuthors.put("Socrates", Arrays.asList(
                "The only true wisdom is in knowing you know nothing."
        ));

        quotesByAuthors.put("Booker T. Washington", Arrays.asList(
                "If you want to lift yourself up, lift up someone else."
        ));

        quotesByAuthors.put("Albert Schweitzer", Arrays.asList(
                "Success is not the key to happiness. Happiness is the key to success. If you love what you are doing, you will be successful."
        ));

        quotesByAuthors.put("Dalai Lama", Arrays.asList(
                "The purpose of our lives is to be happy."
        ));

        quotesByAuthors.put("John C. Maxwell", Arrays.asList(
                "A leader is one who knows the way, goes the way, and shows the way."
        ));

        quotesByAuthors.put("Winston Churchill", Arrays.asList(
                "Success is not final, failure is not fatal: It is the courage to continue that counts.",
                "The pessimist sees difficulty in every opportunity. The optimist sees opportunity in every difficulty."
        ));

        quotesByAuthors.put("Henry Ford", Arrays.asList(
                "Whether you think you can, or you think you can't – you're right.",
                "Failure is simply the opportunity to begin again, this time more intelligently."
        ));

        quotesByAuthors.put("Thomas Edison", Arrays.asList(
                "I have not failed. I've just found 10,000 ways that won't work.",
                "Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time."
        ));

    }


        private void setRandomQuote() {
        // Получаем список всех имен авторов
        List<String> authors = new ArrayList<>(quotesByAuthors.keySet());

        // Получаем случайного автора
        Random random = new Random();
        String randomAuthor = authors.get(random.nextInt(authors.size()));

        // Получаем цитаты этого автора
        List<String> quotes = quotesByAuthors.get(randomAuthor);

        // Получаем случайную цитату
        String randomQuote = quotes.get(random.nextInt(quotes.size()));

        // Устанавливаем случайную цитату и имя автора в TextView
        quoteText.setText(randomQuote);
        authorText.setText("- " + randomAuthor);
    }
}
