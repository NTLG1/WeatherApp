package com.example.usthweather;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ApiKeyInputDialogFragment extends DialogFragment  {

    public interface ApiKeyListener {
        void onSaveApiKey(String apiKey);
        void onDismiss();
    }

    private ApiKeyListener apiKeyListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            apiKeyListener = (ApiKeyListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ApiKeyListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_api_key_input, null);

        final EditText editTextApiKey = view.findViewById(R.id.editTextApiKey);
        Button btnSaveApiKey = view.findViewById(R.id.btnSaveApiKey);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnSaveApiKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiKey = editTextApiKey.getText().toString();
                apiKeyListener.onSaveApiKey(apiKey);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiKeyListener.onDismiss();
                dismiss();
            }
        });

        AlertDialog dialog = builder.setView(view)
                .setTitle(R.string.enterAPI)
                .create();

        dialog.setCanceledOnTouchOutside(false); // Set dialog not cancelable on touch outside

        return dialog;
    }


}


