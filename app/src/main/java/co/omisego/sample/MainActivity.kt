package co.omisego.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.omisego.omisego.Callback
import co.omisego.omisego.NewOMGAPIClient
import co.omisego.omisego.models.*
import co.omisego.omisego.networks.core.ewallet.EWalletClient
import co.omisego.omisego.utils.OMGEncryptionHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = OMGEncryptionHelper.encryptBase64(
                "LFBcGM9chCf39XY0saZFbZPOjF_kKNxvun61yGqafpo",
                "-ewI2XoyTztIAJORaQC6cTyMvMUiCGG4NVD26rC_AkY"
        )

        val eWalletClient = EWalletClient.Builder {
            authenticationToken = auth
            baseURL = "https://ewallet.staging.omisego.io/api/"
            debug = true
        }.build()

        val omgApiClient = NewOMGAPIClient(eWalletClient)
//        omgApiClient.getCurrentUser(object : Callback<User> {
//            override fun success(response: Response<User>) {
//                print(response)
//            }
//
//            override fun fail(response: Response<ApiError>) {
//                println(response)
//            }
//        })

        omgApiClient.getSetting(object : Callback<Setting> {
            override fun success(response: Response<Setting>) {
                print(response)
            }

            override fun fail(response: Response<ApiError>) {
                println(response)
            }
        })
    }
}
