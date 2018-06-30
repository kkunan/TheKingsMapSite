package antvk.tkms.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import antvk.tkms.Activities.MarkerEventListActivity;
import antvk.tkms.R;

public interface UIUtils {

    static void createAndShowAlertDialog(Context context,
                                         String headerMessage,
                                         String descriptionMessage,
                                         DialogInterface.OnClickListener positiveAction,
                                         DialogInterface.OnClickListener negativeAction)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(headerMessage)
                .setMessage(descriptionMessage);

        if(positiveAction != null) {
                builder.setPositiveButton("Yes", positiveAction);
        }
        if(negativeAction != null) {
                builder.setNegativeButton("No", negativeAction);
        }

        builder.create().show();

    }
}
