package com.browncup.chatonx;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.util.Random;

public class MainActivity extends AppCompatActivity  {
    private String channelID = "bbEuEzjzbV3mCVAb";
    private String roomName = "solo1";
    private EditText editText;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private MemberData data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This is where we write the mesage
        editText = (EditText) findViewById(R.id.editText);
        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        SharedPreferences sharedpreferences = getSharedPreferences("P1", Context.MODE_PRIVATE);
        String name= sharedpreferences.getString("username",getRandomName());
         data = new MemberData(name, getRandomColor());
        scaledrone = new Scaledrone(channelID,data);
        System.out.println("-----ScaleDrone--------");
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                scaledrone.subscribe("solo1", new RoomListener() {
                    @Override
                    public void onOpen(Room room) {
                        room.publish("Connected");
                        System.out.println("Conneted to room");
                    }

                    @Override
                    public void onOpenFailure(Room room, Exception ex) {
                        // This can happen when you don't have correct permissions
                        System.out.println("Failed to subscribe to room: " + ex.getMessage());

                    }

                    @Override
                    public void onMessage(Room room, JsonNode json, Member member) {
                        System.out.println("Message: " + json.asText());

                        // To transform the raw JsonNode into a POJO we can use an ObjectMapper
                        final ObjectMapper mapper = new ObjectMapper();
                        //if(member.getClientData()==null) return;
                        try {



                            boolean belongsToCurrentUser = member.getId().equals(scaledrone.getClientID());
                            // since the message body is a simple string in our case we can use json.asText() to parse it as such
                            // if it was instead an object we could use a similar pattern to data parsing
                            final Message message = new Message(json.asText(), data, belongsToCurrentUser);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messageAdapter.add(message);
                                    // scroll the ListView to the last added element
                                    messagesView.setSelection(messagesView.getCount() - 1);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.out.println("Failed to open connection: " + ex.getMessage());
            }

            @Override
            public void onFailure(Exception ex) {
                System.out.println("Unexcpected failure: " + ex.getMessage());
            }

            @Override
            public void onClosed(String reason) {
                System.out.println("Connection closed: " + reason);
            }


        });

    }







    private String getRandomName() {
        String[] adjs = {"autumn", "hidden", "bitter", "misty", "silent", "empty", "dry", "dark", "summer", "icy", "delicate", "quiet", "white", "cool", "spring", "winter", "patient", "twilight", "dawn", "crimson", "wispy", "weathered", "blue", "billowing", "broken", "cold", "damp", "falling", "frosty", "green", "long", "late", "lingering", "bold", "little", "morning", "muddy", "old", "red", "rough", "still", "small", "sparkling", "throbbing", "shy", "wandering", "withered", "wild", "black", "young", "holy", "solitary", "fragrant", "aged", "snowy", "proud", "floral", "restless", "divine", "polished", "ancient", "purple", "lively", "nameless"};
        String[] nouns = {"waterfall", "river", "breeze", "moon", "rain", "wind", "sea", "morning", "snow", "lake", "sunset", "pine", "shadow", "leaf", "dawn", "glitter", "forest", "hill", "cloud", "meadow", "sun", "glade", "bird", "brook", "butterfly", "bush", "dew", "dust", "field", "fire", "flower", "firefly", "feather", "grass", "haze", "mountain", "night", "pond", "darkness", "snowflake", "silence", "sound", "sky", "shape", "surf", "thunder", "violet", "water", "wildflower", "wave", "water", "resonance", "sun", "wood", "dream", "cherry", "tree", "fog", "frost", "voice", "paper", "frog", "smoke", "star"};
        return (
                adjs[(int) Math.floor(Math.random() * adjs.length)] +
                        "_" +
                        nouns[(int) Math.floor(Math.random() * nouns.length)]
        );
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();
        if (message.length() > 0) {
            scaledrone.publish("solo1", message);
            editText.getText().clear();
        }
    }

}

class MemberData {
    private String name;
    private String color;

    public MemberData(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberData() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
