package com.bulentsiyah.gesturerecognitionwithandroid;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ViewDialog {

	public Boolean isShowing=false;
	
    public void showDialog(Activity activity,String title, String msg){
    	try{
    		isShowing=true;
    	    final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_red);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);
            
            TextView textViewUyariTitle = (TextView) dialog.findViewById(R.id.textViewUyariTitle);
            textViewUyariTitle.setText(title);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	isShowing=false;
                    dialog.dismiss();
                }
            });

            dialog.show();
    		
    	}catch(Exception exp){
    		
    	}
    

    }
}
