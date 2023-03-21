    package ru.iu3.fclient;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import ru.iu3.fclient.databinding.ActivityMainBinding;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.IOUtils;



    public class MainActivity extends AppCompatActivity implements TransactionEvents{

    // Used to load the 'fclient' library on application startup.
    static {
        System.loadLibrary("fclient");
        System.loadLibrary("mbedcrypto");
    }

        private String pin;

        @Override
        public String enterPin(int ptc, String amount) {
            pin = new String();
            Intent it = new Intent(MainActivity.this, PinpadActivity.class);
            it.putExtra("ptc", ptc);
            it.putExtra("amount", amount);
            synchronized (MainActivity.this) {
                activityResultLauncher.launch(it);
                try {
                    MainActivity.this.wait();
                } catch (Exception ex) {
                    //todo: log error
                }
            }
            return pin;
        }

    private ActivityMainBinding binding;

    //public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);

    ActivityResultLauncher activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int res = initRng();
        byte[] v = randomBytes(10);

        // Example of a call to a native method
        //TextView tv = binding.sampleText;
        //tv.setText(stringFromJNI());


        /*activityResultLauncher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult> () {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            String pin = data.getStringExtra("pin");
                            Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            //String pin = data.getStringExtra("pin");
                            //Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT).show();
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });

    }

    /**
     * A native method that is implemented by the 'fclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    // ЛР-2
    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }

        //public void onButtonClick(View v)
        //{


            //byte[] trd = stringToHex("9F0206000000000100");
            //ransaction(trd);


            /*new Thread(()-> {
                try {
                    byte[] trd = stringToHex("9F0206000000000100");
                    boolean ok = transaction(trd);
                    runOnUiThread(()-> {
                        Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception ex) {
                    // todo: log error
                }
            }).start();*/

            //byte[] key = stringToHex("0123456789ABCDEF0123456789ABCDE0");
            //byte[] enc = encrypt(key, stringToHex("000000000000000102"));
            //byte[] dec = decrypt(key, enc);
            //String s = new String(Hex.encodeHex(dec)).toUpperCase();
            //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
            //Intent it = n ew Intent(this, PinpadActivity.class);
            //startActivity(it);
            //activityResultLauncher.launch(it);
        //}


        @Override
        public void transactionResult(boolean result) {
            runOnUiThread(()-> {
                Toast.makeText(MainActivity.this, result ? "ok" : "failed", Toast.LENGTH_SHORT).show();
            });
        }



        public static native byte[] encrypt(byte[] key, byte[] data);
        public static native byte[] decrypt(byte[] key, byte[] data);

        public native boolean transaction(byte[] trd);



        protected void testHttpClient()
        {
            new Thread(() -> {
                try {
                    HttpURLConnection uc = (HttpURLConnection)
                            (new URL("https://www.wikipedia.org").openConnection());
                    InputStream inputStream = uc.getInputStream();
                    String html = IOUtils.toString(inputStream);
                    String title = getPageTitle(html);
                    runOnUiThread(() ->
                    {
                        Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                    });

                } catch (Exception ex) {
                    Log.e("fapptag", "Http client fails", ex);
                }
            }).start();
        }



        /*protected String getPageTitle(String html)
        {
            int pos = html.indexOf("<title");
            String p="not found";
            if (pos >= 0)
            {
                int pos2 = html.indexOf("<", pos + 1);
                if (pos >= 0)
                    p = html.substring(pos + 7, pos2);
            }
            return p;
        }*/



        protected String getPageTitle(String html)
        {
            Pattern pattern = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(html);
            String p;
            if (matcher.find())
                p = matcher.group(1);
            else
                p = "Not found";
            return p;
        }




        public void onButtonClick(View v)
        {
            testHttpClient();
            //byte[] trd = stringToHex("9F0206000000000100");
            //transaction(trd);


        /*new Thread(()-> {
            try {
                byte[] trd = stringToHex("9F0206000000000100");
                boolean ok = transaction(trd);
                runOnUiThread(()-> {
                    Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception ex) {
                // todo: log error
            }
        }).start();
        Intent it = new Intent(this, PinpadActivity.class);
        //startActivity(it);
        activityResultLauncher.launch(it);*/
        }




    }






