package com.levin.books;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ApiUtil {
    private static final String QUERY_PARAMETER_KEY = "q";

    private ApiUtil() {}

    public  static  final String BASE_API_URL = "https://www.googleapis.com/books/v1/volumes";
    public  static  final String KEY = "key";
    public  static  final String API_KEY = "AIzaSyAecRM7X0QQSClYAX0vpmu5baxkgl5gIsc";

    public  static URL buildUrl(String title) throws MalformedURLException {
        Uri uri = Uri.parse(BASE_API_URL)
                .buildUpon()
                .appendQueryParameter(QUERY_PARAMETER_KEY, title)
                .appendQueryParameter(KEY, API_KEY)
                .build();

        try{
            return  new URL(uri.toString());
        }
        catch (Exception e){
            Log.d("Error", e.getMessage());
            return null;
        }
    }

    public static ArrayList<Book> getBooksFromJson(String json){
        final String ID = "id";
        final String TITLE = "title";
        final String SUBTITLE = "subtitle";
        final String AUTHORS = "authors";
        final String PUBLISHER = "publisher";
        final String PUBLISHED_DATE = "publishedDate";
        final String ITEMS = "items";
        final String VOLUMEINFO = "volumeInfo";

        ArrayList<Book> books = new ArrayList<Book>();

        try{
            JSONObject jsonBooks = new JSONObject(json);
            JSONArray arrayBooks = jsonBooks.getJSONArray(ITEMS);
            int numberOfBooks = arrayBooks.length();

            for(int i = 0; i < numberOfBooks; i++){
                JSONObject bookJSON = arrayBooks.getJSONObject(i);
                JSONObject volumeInfo = bookJSON.getJSONObject(VOLUMEINFO);

                int authorsNum = volumeInfo.getJSONArray(AUTHORS).length();
                String[] authors = new String[authorsNum];

                for (int j = 0; j < authorsNum; j++){
                    authors[j] = volumeInfo.getJSONArray(AUTHORS).get(j).toString();
                }

                Book book = new Book(
                        bookJSON.getString(ID),
                        volumeInfo.getString(TITLE),
                        volumeInfo.isNull(SUBTITLE) ? "" : volumeInfo.getString(SUBTITLE),
                        authors,
                        volumeInfo.getString(PUBLISHER),
                        volumeInfo.getString(PUBLISHED_DATE)
                );

                books.add(book);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return books;
    }

    public  static  String getJson(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try{
            InputStream stream = connection.getInputStream();
            Scanner scanner = new Scanner(stream);
            scanner.useDelimiter("\\A");

            boolean hasData = scanner.hasNext();

            if (hasData){
                return scanner.next();
            }
            else {
                return  null;
            }
        }
        catch (Exception e){
            Log.d("Error", e.toString());
            return  null;
        }
        finally {
            connection.disconnect();
        }
    }
}
