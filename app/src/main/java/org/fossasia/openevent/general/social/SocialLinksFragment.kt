package org.fossasia.openevent.general.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_social_links.eventHostDetails
import kotlinx.android.synthetic.main.fragment_social_links.socialLinksRecycler
import kotlinx.android.synthetic.main.fragment_social_links.view.progressBarSocial
import kotlinx.android.synthetic.main.fragment_social_links.view.socialLinkReload
import kotlinx.android.synthetic.main.fragment_social_links.view.socialLinksRecycler
import kotlinx.android.synthetic.main.fragment_social_links.view.socialNoInternet
import org.fossasia.openevent.general.R
import org.fossasia.openevent.general.event.EVENT_ID
import org.fossasia.openevent.general.utils.extensions.nonNull
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SocialLinksFragment : Fragment() {
    private val socialLinksRecyclerAdapter: SocialLinksRecyclerAdapter = SocialLinksRecyclerAdapter()
    private val socialLinksViewModel by viewModel<SocialLinksViewModel>()
    private lateinit var rootView: View
    private var id: Long = -1
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var showErrorSnack: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getLong(EVENT_ID, -1)
        }
        loadSocialLink()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_social_links, container, false)

        rootView.progressBarSocial.isIndeterminate = true

        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rootView.socialLinksRecycler.layoutManager = linearLayoutManager

        rootView.socialLinksRecycler.adapter = socialLinksRecyclerAdapter
        rootView.socialLinksRecycler.isNestedScrollingEnabled = false

        rootView.socialLinkReload.setOnClickListener {
            loadSocialLink()
        }

        socialLinksViewModel.progress
            .nonNull()
            .observe(viewLifecycleOwner, Observer {
                rootView.progressBarSocial.isVisible = it
            })

        socialLinksViewModel.socialLinks
            .nonNull()
            .observe(viewLifecycleOwner, Observer {
                socialLinksRecyclerAdapter.addAll(it)
                handleVisibility(it)
                socialLinksRecyclerAdapter.notifyDataSetChanged()
                Timber.d("Fetched social-links of size %s", socialLinksRecyclerAdapter.itemCount)
            })

        socialLinksViewModel.error
            .nonNull()
            .observe(viewLifecycleOwner, Observer {
                showErrorSnack?.invoke(it)
            })

        socialLinksViewModel.internetError
            .nonNull()
            .observe(viewLifecycleOwner, Observer {
                rootView.socialNoInternet.isVisible = it
            })

        return rootView
    }

    /*
        function to set errorSnackMessage CallBack, to be invoked ,
        to be invoked when snack error is generated
     */
    fun setErrorSnack(errorSnack: (String) -> Unit) {
        showErrorSnack = errorSnack
    }

    private fun handleVisibility(socialLinks: List<SocialLink>) {
        eventHostDetails.isGone = socialLinks.isEmpty()
        socialLinksRecycler.isGone = socialLinks.isEmpty()
    }

    private fun loadSocialLink() {
        socialLinksViewModel.checkAndLoadSocialLinks(id)
    }
}
