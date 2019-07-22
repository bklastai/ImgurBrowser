package com.bklastai.imgurbrowser.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bklastai.imgurbrowser.R
import com.bklastai.imgurbrowser.views.IMAGE_LINK
import com.bklastai.imgurbrowser.views.IMAGE_TITLE
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_image_viewing.*
import kotlinx.android.synthetic.main.search_result_item.view.*

class ImageViewingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewing)

        Picasso.get().load(intent.getStringExtra(IMAGE_LINK) ?: "").into(image)
        image_title.text = intent.getStringExtra(IMAGE_TITLE) ?: ""
        back_arrow.setOnClickListener {
            finish()
        }
    }
}