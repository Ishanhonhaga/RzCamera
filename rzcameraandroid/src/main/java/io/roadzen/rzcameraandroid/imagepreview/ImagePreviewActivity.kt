package io.roadzen.rzcameraandroid.imagepreview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.roadzen.rzcameraandroid.R
import io.roadzen.rzcameraandroid.util.GlideApp
import io.roadzen.rzcameraandroid.util.Orientation
import io.roadzen.rzcameraandroid.util.getRotation
import io.roadzen.rzcameraandroid.util.getViewModel
import kotlinx.android.synthetic.main.activity_image_preview.*

class ImagePreviewActivity : AppCompatActivity() {

    private val viewModel: ImagePreviewViewModel by lazy { getViewModel { ImagePreviewViewModel() } }

    private lateinit var listAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        setupList()

        viewModel.captureViewState.observe(this, Observer { render(it) })
        viewModel.captureViewEffect.observe(this, Observer { handleEffect(it) })

        deleteButton?.setOnClickListener { viewModel.onEvent(ImagePreviewEvent.DeleteCurrentImage) }
    }

    override fun onStart() {
        super.onStart()
        viewModel.onEvent(ImagePreviewEvent.ScreenLoadEvent)
    }

    private fun handleEffect(viewEffect: ImagePreviewViewEffect?) {
        if (viewEffect == null) return

        when (viewEffect) {
            is ImagePreviewViewEffect.CloseScreenEffect -> finish()
        }
    }

    private fun render(viewState: ImagePreviewViewState?) {
        if (viewState == null) return

        GlideApp.with(this).load(viewState.imagePreviewUri).into(previewImage)
        listAdapter.submitList(viewState.imageUriList)

        if (viewState.isDeleting) {
            deleteProgressbar?.visibility = View.VISIBLE
            deleteButton?.visibility = View.GONE
        } else {
            deleteProgressbar?.visibility = View.GONE
            deleteButton?.visibility = View.VISIBLE
        }
    }

    private fun setupList() {
        val orientation = getRotation(this)
        val linearLayoutManager = if (orientation == Orientation.LANDSCAPE || orientation == Orientation.LANDSCAPE_REVERSE) {
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        } else {
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        }

        previewRecyclerView?.layoutManager = linearLayoutManager
        listAdapter = ImageAdapter { uriStr -> viewModel.onEvent(ImagePreviewEvent.ImageTappedEvent(uriStr)) }
        previewRecyclerView?.adapter = listAdapter
    }
}
