package org.fossasia.openevent.general.auth

import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.fossasia.openevent.general.MainActivity
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.order.LAUNCH_TICKETS
import org.fossasia.openevent.general.ticket.EVENT_ID
import org.fossasia.openevent.general.ticket.TICKET_ID_AND_QTY
import org.fossasia.openevent.general.utils.Utils
import org.fossasia.openevent.general.utils.Utils.hideSoftKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel

const val LAUNCH_ATTENDEE: String = "LAUNCH_ATTENDEE"
class LoginFragment : Fragment() {

    private val loginViewModel by viewModel<LoginViewModel>()
    private lateinit var rootView: View
    private var bundle: Bundle? = null
    private var ticketIdAndQty: List<Pair<Int, Int>>? = null
    private var id: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null && !bundle.getBoolean(LAUNCH_TICKETS)) {
            id = bundle.getLong(EVENT_ID, -1)
            ticketIdAndQty = bundle.getSerializable(TICKET_ID_AND_QTY) as List<Pair<Int, Int>>
        }
        this.bundle = bundle
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)

        if (loginViewModel.isLoggedIn())
            redirectToMain(bundle)

        rootView.loginButton.setOnClickListener {
            loginViewModel.login(email.text.toString(), password.text.toString())
            hideSoftKeyboard(context, rootView)
        }

        loginViewModel.progress.observe(this, Observer {
            it?.let {
                Utils.showProgressBar(rootView.progressBar, it)
                loginButton.isEnabled = !it
            }
        })

        loginViewModel.showNoInternetDialog.observe(this, Observer {
            Utils.showNoInternetDialog(context)
        })

        loginViewModel.error.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        loginViewModel.loggedIn.observe(this, Observer {
            Toast.makeText(context, getString(R.string.welcome_back) , Toast.LENGTH_LONG).show()
            loginViewModel.fetchProfile()
        })

        rootView.email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(email: CharSequence, start: Int, before: Int, count: Int) {
                loginViewModel.checkEmail(email.toString())
            }
        })

        loginViewModel.requestTokenSuccess.observe(this, Observer {
            rootView.sentEmailLayout.visibility = View.VISIBLE
            rootView.loginLayout.visibility = View.GONE
        })

        loginViewModel.isCorrectEmail.observe(this, Observer {
            it?.let {
                onEmailEntered(it)
            }
        })

        rootView.tick.setOnClickListener {
            rootView.sentEmailLayout.visibility = View.GONE
            rootView.loginLayout.visibility = View.VISIBLE
        }

        rootView.forgotPassword.setOnClickListener {
            hideSoftKeyboard(context, rootView)
            loginViewModel.sendResetPasswordEmail(email.text.toString())
        }

        loginViewModel.user.observe(this, Observer {
            redirectToMain(bundle)
        })

        return rootView
    }

    private fun redirectToMain(bundle: Bundle?) {
        val intent = Intent(activity, MainActivity::class.java)
        if (bundle != null) {
            if (!id.equals(-1) && ticketIdAndQty != null) {
                intent.putExtra(LAUNCH_ATTENDEE, true)
            }
            intent.putExtras(bundle)
        }
        startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        activity?.finish()
    }

    private fun onEmailEntered(enable: Boolean) {
        rootView.loginButton.isEnabled = enable
        rootView.forgotPassword.visibility = if (enable) View.VISIBLE else View.GONE
    }
}
