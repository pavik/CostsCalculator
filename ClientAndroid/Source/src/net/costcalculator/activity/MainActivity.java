/**
 * The author or authors of this code dedicate any and all copyright interest
 * in this code to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and successors. 
 * We intend this dedication to be an overt act of relinquishment in perpetuity 
 * of all present and future rights to this code under copyright law.
 */

package net.costcalculator.activity;

import net.costcalculator.activity.R;
import net.costcalculator.util.LOG;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

/**
 * Application entry point, starts other activites,
 * allocate global resources on start and release on destroy.
 * 
 * @author Aliaksei Plashchanski
 *
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		firstRun = true;
		
		// initialize global resources
		LOG.INITIALIZE();
		LOG.T("MainActivity::onCreate");
		
		// start main activity
		startActivity(new Intent(this, ExpenseItemsActivity.class));
	}

	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first

	    LOG.T("MainActivity::onResume");
	    
	    // main activity resumed second time, we should quit application
	    if (!firstRun)
	    	finish();
	    else
	    	firstRun = false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// release global resources
		LOG.T("MainActivity::onDestroy");
		LOG.RELEASE();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private boolean firstRun;
}
