package com.shak.taskmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.models.RegisterBenefitsItemModel

class RegisterBenefitsViewPagerAdapter(
    private val itemsList: List<RegisterBenefitsItemModel>
) : RecyclerView.Adapter<RegisterBenefitsViewPagerAdapter.RegisterBenefitsViewPagerViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RegisterBenefitsViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.register_benefits_item_layout, parent, false)
        return RegisterBenefitsViewPagerViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RegisterBenefitsViewPagerViewHolder,
        position: Int
    ) {
        val currentItemOfList = itemsList[position]

        holder.counter.text = "${position + 1} of ${itemsList.size}"
        holder.image.setImageResource(currentItemOfList.imageRes)
        holder.title.text = currentItemOfList.description
    }

    override fun getItemCount() = itemsList.size


    inner class RegisterBenefitsViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val counter: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.benefitCounterTxt)
        val image: AppCompatImageView = itemView.findViewById<AppCompatImageView>(R.id.benefitImg)
        val title: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.benefitDescriptionTxt)
    }
}