package street.food.googlebilling;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    Button payButtonbasic;
    List<SkuDetails> list;
    BillingClient billingClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpBillingClient();
        payButtonbasic = findViewById(R.id.btn_pay_basic1);

        payButtonbasic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(list.get(0))
                        .build();
                billingClient.launchBillingFlow(MainActivity.this,billingFlowParams);
            }
        });
    }

    private void setUpBillingClient() {
        billingClient = BillingClient.newBuilder(this)
                .setListener(this).enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    // Toast.makeText(GooglePaymentActivity.this, "Success to connect Billing", Toast.LENGTH_SHORT).show();
                    checkBillingClien();}
                else
                    Toast.makeText(MainActivity.this, ""+billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(MainActivity.this, "You are disconnect from Billing", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkBillingClien(){
        if(billingClient.isReady())
        {
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(Arrays.asList("Your_Product_id1","Your_Product_id2"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build();
            billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skulist) {
                    if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                        list = skulist;
                    }else{
                        Toast.makeText(MainActivity.this, "Can't query plan", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(MainActivity.this, "Billing client not ready", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
        {
             Toast.makeText(this, "Item is Purchased", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Plan purchased is cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
