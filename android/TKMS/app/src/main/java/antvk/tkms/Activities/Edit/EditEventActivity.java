package antvk.tkms.Activities.Edit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.util.Calendar;

import antvk.tkms.Activities.AddStuffsActivity;
import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.PlaceItem;
import antvk.tkms.Utils.ClassMapper;

import static antvk.tkms.Constants.MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE;
import static antvk.tkms.Constants.PICK_IMAGE;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditEventActivity extends AddStuffsActivity {

    Bundle b = new Bundle();

    View customView;

    EditText eventName, eventDescription;
    ImageView eventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        if(currentEvent == null)
            currentEvent = new PlaceItem.Event();

        eventName = (EditText)findViewById(R.id.event_name_edittext);
        eventDescription = (EditText)findViewById(R.id.event_description_edittext);
        eventImage = (ImageView)findViewById(R.id.event_image);
    }

    @Override
    Bundle setFurtherExtra(Bundle b) {
        b.putString(MAP_KEY,gson.toJson(currentMap));
        b.putString(PLACE_KEY,gson.toJson(currentItem));

        //this is because we want the next one to go back to the EditMap page, not here
        b.putString(ClassMapper.classIntentKey, "EditMapActivity");

        return b;
    }

    public void onSubmitButtonClick(View view) {
        currentEvent.title = eventName.getText().toString();
        currentEvent.description = eventDescription.getText().toString();

        String previousClass = getIntent().getStringExtra(ClassMapper.classIntentKey);

        if(currentEvent.id < 0)
        {
            currentEvent.id = currentItem.events.size();
            currentItem.events.add(currentEvent);
        }

        else
        {
            currentItem.events.set(currentEvent.id,currentEvent);
        }

        // TODO: 07/06/2018 set event up

        gobackToPreviousScreen();
    }

    public void onDateTimeSelectorClick(View view) {

        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        customView = inflater.inflate(R.layout.layout_datetime_picker, null);

        // Define your date pickers
        final DatePicker dpDate = customView.findViewById(R.id.event_date_picker);
        final TimePicker dpTime = customView.findViewById(R.id.event_time_picker);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(customView); // Set the view of the dialog to your custom layout
        builder.setTitle("Select event date and time");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentEvent.date = getDate(dpDate);
                currentEvent.dateFormat = Constants.DMY_DATE_FORMATTER.toPattern();
                currentEvent.time = getTime(dpTime);

                Button p1_button = findViewById(R.id.event_datetime_button);
                p1_button.setText(currentEvent.date+"T"+currentEvent.time);
                dialog.dismiss();
            }

            private String add0IfNecessary(int value)
            {
                if(value<=9)
                    return "0"+value;
                else return value+"";
            }

            private String getTime(TimePicker dpTime) {
                int hour, min;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    hour = dpTime.getHour();
                    min = dpTime.getMinute();
                }

                else {
                    hour = dpTime.getCurrentHour();
                    min = dpTime.getCurrentMinute();
                }
                return String.format("%s:%s",add0IfNecessary(hour),add0IfNecessary(min));
            }

            private String getDate(DatePicker datePicker) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year =  datePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                return Constants.DMY_DATE_FORMATTER.format(calendar.getTime());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
        });

        // Create and show the dialog
        builder.create().show();

    }

    public void showDateHideTime(View view) {
        customView.findViewById(R.id.event_date_picker).setVisibility(View.VISIBLE);
        customView.findViewById(R.id.event_time_picker).setVisibility(View.GONE);
        customView.findViewById(R.id.show_date_button).setBackgroundColor(Color.TRANSPARENT);
        customView.findViewById(R.id.show_time_button).setBackgroundColor(ContextCompat.getColor(EditEventActivity.this,R.color.heart_grey));
    }

    public void showTimeHideDate(View view) {
        customView.findViewById(R.id.event_date_picker).setVisibility(View.GONE);
        customView.findViewById(R.id.event_time_picker).setVisibility(View.VISIBLE);
        customView.findViewById(R.id.show_time_button).setBackgroundColor(Color.TRANSPARENT);
        customView.findViewById(R.id.show_date_button).setBackgroundColor(ContextCompat.getColor(EditEventActivity.this,R.color.heart_grey));
    }

    public void onEventImageClick(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditEventActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE);
        }

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Event Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void selectPicAction(String picturePath) {
        int id = currentEvent.id;
        currentEvent.imageName = resizeAndGet(picturePath,"event",id);
        eventImage.setImageURI(Uri.parse(currentEvent.imageName));
    }

}
