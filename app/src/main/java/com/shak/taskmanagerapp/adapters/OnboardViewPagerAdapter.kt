package com.shak.taskmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.models.OnboardingItemModel

class OnboardViewPagerAdapter(
    private val itemsList: List<OnboardingItemModel>
): RecyclerView.Adapter<OnboardViewPagerAdapter.OnboardViewPagerViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnboardViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.onboard_item_layout, parent, false)
        return OnboardViewPagerViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: OnboardViewPagerViewHolder,
        position: Int
    ) {
        val item = itemsList[position]

        holder.counter.text = "${position + 1} of ${itemsList.size}"
        holder.image.setImageResource(item.imageRes)
        holder.title.text = item.title
        holder.description.text = item.description
    }

    override fun getItemCount() = itemsList.size

    inner class OnboardViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counter: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.onboardCounterTxt)
        val image: AppCompatImageView = itemView.findViewById<AppCompatImageView>(R.id.onboardImg)
        val title: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.onboardTitleTxt)
        val description: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.onboardDescriptionTxt)
    }


}