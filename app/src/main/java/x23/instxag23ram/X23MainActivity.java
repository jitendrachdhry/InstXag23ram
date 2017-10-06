package x23.instxag23ram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class X23MainActivity extends AppCompatActivity {

    //Register the app by following below link.
    //http://instagram.com/developer/
    //After registration copy client_id, client_secret and callback_rul from instagram Manage Client page.

    public static final String X23_IG_CLIENT_ID = "19ac6ed774524e5c97798c5d20dfaa6c";
    public static final String X23_IG_CLIENT_SECRET = "2f193576dd0341508fc4062bbe5d5464";
    public static final String X23_IG_CALLBACK_URL = "https://www.23andme.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_x23_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_x23_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            // Exit from X23MainActivity
            finish();
            return true;
        } else if (id == R.id.action_about) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.action_about_txt), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (id == R.id.action_help) {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.action_help_txt), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }
}
