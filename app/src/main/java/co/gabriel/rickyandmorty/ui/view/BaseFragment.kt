package co.gabriel.rickyandmorty.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable

open class BaseFragment : Fragment() {

    fun showError(message: String?) {
        if (message != null) {
            Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
        }
    }

    fun showLoading() {
        Snackbar.make(this.requireView(), "Loading..", Snackbar.LENGTH_LONG).show()
    }

    fun <T : Serializable> Bundle?.getSerializableCompat(key: String, clazz: Class<T>): T? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            this?.getSerializable(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            this?.getSerializable(key) as? T
        }
    }

}