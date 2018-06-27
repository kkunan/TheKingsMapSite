package antvk.tkms.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import antvk.tkms.Constants;
import antvk.tkms.R;
import antvk.tkms.Struct.Information.InformationItem;
import antvk.tkms.Struct.MapAttribute.AvailableMap;
import antvk.tkms.Utils.ClassMapper;

import static antvk.tkms.Activities.MapSelectorActivity.*;
import static antvk.tkms.Constants.MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE;
import static antvk.tkms.Constants.PICK_IMAGE;

@RequiresApi(api = Build.VERSION_CODES.N)
public class EditEventActivity extends ActivityWithBackButton {

    AvailableMap map;
    InformationItem place;
    InformationItem.Event event;
    int mapIndex = -1, placeIndex = -1, itemID = -1;
    Bundle b = new Bundle();

    View customView;

    EditText eventName, eventDescription;
    ImageView eventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        itemID = getExtra(MARKER_KEY);
        mapIndex = getExtra(MAP_ID_KEY);

        if(mapIndex >= 0 && mapIndex < maps.size())
        {
            map = maps.get(mapIndex);
        }

        if(placeIndex > 0)
        {
            place = map.informationItems.get(placeIndex);
        }

        if(itemID > 0)
        {
            event = place.events.get(itemID);
        }

        else event = new InformationItem.Event();

        eventName = (EditText)findViewById(R.id.event_name_edittext);
        eventDescription = (EditText)findViewById(R.id.event_description_edittext);
        eventImage = (ImageView)findViewById(R.id.event_image);
    }

    @Override
    public void onBackPressed() {
        gobackToPreviousScreen();
    }

    private void gobackToPreviousScreen() {
        String previousClass = getIntent().getStringExtra(ClassMapper.classIntentKey);
        Class cl = ClassMapper.get(previousClass);

        b.putInt(MAP_ID_KEY,mapIndex);
        b.putInt(MARKER_KEY,placeIndex);

        Intent intent = new Intent(getApplicationContext(),cl);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void onSubmitButtonClick(View view) {
        event.title = eventName.getText().toString();
        event.description = eventDescription.getText().toString();

        String itemJson = new Gson().toJson(event);

        // TODO: 07/06/2018 set event up

        if(itemID>=0)
            b.putInt(EVENT_KEY,itemID);

        b.putString(SUB_ITEM, itemJson);

        System.out.println(itemJson);
        gobackToPreviousScreen();
    }

    public void onDateTimeSelectorClick(View view) {

        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        customView = inflater.inflate(R.layout.datetime_picker_layout, null);

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
                event.date = getDate(dpDate);
                event.dateFormat = Constants.DMY_DATE_FORMATTER.toPattern();
                event.time = getTime(dpTime);

                Button p1_button = findViewById(R.id.event_datetime_button);
                p1_button.setText(event.date+"T"+event.time);
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

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Map Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData( );
                String picturePath = EditMapActivity.getPath(EditEventActivity.this, selectedImageUri );
                System.out.println("Picture Path"+ picturePath);

                if(place.events == null)
                    place.events = new ArrayList<>();

                int id = itemID>=0?itemID:place.events.size();

                event.imageName = resizeAndGet(picturePath,"event",id);
                eventImage.setImageURI(Uri.parse(event.imageName));
            }

        }
    }
    public String resizeAndGet(String realPath,String type, int id)
    {
        Bitmap b= BitmapFactory.decodeFile(realPath);
        int width = b.getWidth();
        int height = b.getHeight();

        int newHeight = 200;
        int newWidth = (int)((double)(width)/(double)(height) * newHeight);
        Bitmap out = Bitmap.createScaledBitmap(b, newWidth, newHeight, false);

        File dir= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/"+getResources().getString(R.string.app_name));
        System.out.println("folder: "+dir);
        dir.mkdir();
        File file = new File(dir, type+id+".png");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
