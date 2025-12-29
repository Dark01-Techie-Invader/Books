package com.example.assignmentno03;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText titleEt, authorEt, isbnEt, yearEt;
    Button addBookBtn, logoutBtn;
    RecyclerView recyclerView;

    FirebaseAuth auth;
    FirebaseFirestore db;
    ArrayList<Book> bookList = new ArrayList<>();
    BookAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titleEt = findViewById(R.id.titleEt);
        authorEt = findViewById(R.id.authorEt);
        isbnEt = findViewById(R.id.isbnEt);
        yearEt = findViewById(R.id.yearEt);
        addBookBtn = findViewById(R.id.addBookBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        recyclerView = findViewById(R.id.bookRecycler);

        adapter = new BookAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        addBookBtn.setOnClickListener(v -> addBook());
        logoutBtn.setOnClickListener(v -> logoutUser());

        loadBooks();
    }

    private void logoutUser() {
        auth.signOut();
        finish(); // go back to login
    }

    private void addBook() {
        String title = titleEt.getText().toString().trim();
        String author = authorEt.getText().toString().trim();
        String isbn = isbnEt.getText().toString().trim();
        String year = yearEt.getText().toString().trim();

        if(title.isEmpty() || author.isEmpty()){
            Toast.makeText(this,"Title & Author required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,Object> book = new HashMap<>();
        book.put("title", title);
        book.put("author", author);
        book.put("isbn", isbn);
        book.put("year", year);
        book.put("userId", auth.getUid());

        db.collection("books").add(book)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this,"Book Added", Toast.LENGTH_SHORT).show();
                    clearFields();
                    loadBooks();
                })
                .addOnFailureListener(e -> Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadBooks() {
        bookList.clear();
        db.collection("books")
                .whereEqualTo("userId", auth.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    for(DocumentSnapshot doc: snapshot){
                        Book book = doc.toObject(Book.class);
                        book.id = doc.getId();
                        bookList.add(book);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void clearFields(){
        titleEt.setText("");
        authorEt.setText("");
        isbnEt.setText("");
        yearEt.setText("");
    }

    // Book Model
    class Book {
        String id, title, author, isbn, year, userId;
        public Book(){}
    }

    // RecyclerView Adapter
    class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookVH>{

        @NonNull
        @Override
        public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_book, parent, false);
            return new BookVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BookVH holder, int position) {
            Book book = bookList.get(position);
            holder.titleTv.setText(book.title);
            holder.authorTv.setText("Author: " + book.author);
            holder.isbnTv.setText("ISBN: " + book.isbn);
            holder.yearTv.setText("Year: " + book.year);

            holder.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete Book")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", (d,i) ->
                                db.collection("books").document(book.id).delete()
                                        .addOnSuccessListener(a -> {
                                            Toast.makeText(MainActivity.this,"Book Deleted", Toast.LENGTH_SHORT).show();
                                            loadBooks();
                                        }))
                        .setNegativeButton("No",null)
                        .show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return bookList.size();
        }

        class BookVH extends RecyclerView.ViewHolder{
            TextView titleTv, authorTv, isbnTv, yearTv;
            public BookVH(@NonNull View itemView){
                super(itemView);
                titleTv = itemView.findViewById(R.id.titleTv);
                authorTv = itemView.findViewById(R.id.authorTv);
                isbnTv = itemView.findViewById(R.id.isbnTv);
                yearTv = itemView.findViewById(R.id.yearTv);
            }
        }
    }
}
