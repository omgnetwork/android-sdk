package co.omisego.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import co.omisego.omisego.custom.Callback
import co.omisego.omisego.NewOMGAPIClient
import co.omisego.omisego.model.*
import co.omisego.omisego.network.ewallet.EWalletClient
import co.omisego.omisego.utils.EncryptionHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = EncryptionHelper.encryptBase64(
                "LFBcGM9chCf39XY0saZFbZPOjF_kKNxvun61yGqafpo",
                "-ewI2XoyTztIAJORaQC6cTyMvMUiCGG4NVD26rC_AkY"
        )

        val eWalletClient = EWalletClient.Builder {
            authenticationToken = auth
            baseURL = "https://ewallet.staging.omisego.io/api/"
            debug = true
        }.build()

        val omgApiClient = NewOMGAPIClient(eWalletClient)

//        omgApiClient.getSetting(object : Callback<Setting> {
//            override fun success(response: OMGResponse<Setting>) {
//                print(response)
//            }
//
//            override fun fail(response: OMGResponse<ApiError>) {
//                println(response)
//            }
//        })

        omgApiClient.getCurrentUser(object: Callback<User> {
            override fun success(response: OMGResponse<User>) {
                println(response.data.username)
            }

            override fun fail(response: OMGResponse<ApiError>) {
            }

        })
    }
}
