package com.example.beering.feature.drink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.beering.feature.review.drinkDetailReviews.DrinkDetailReviewsActivity
import com.example.beering.R
import com.example.beering.feature.review.reviewWriting.ReviewWritingActivity
import com.example.beering.util.getAccessToken
import com.example.beering.databinding.ActivityDrinkDetailBinding
import com.example.beering.util.getRetrofit_header
import retrofit2.Call
import retrofit2.Response


class DrinkDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityDrinkDetailBinding

    var isInterest = false

    var reviewAdapter: ReviewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrinkDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 데이터를 받아서 처리
        val intent = intent
        var drinkId : Int? = null

        if (intent != null) {
            drinkId = intent.getIntExtra("drinkId", -1)
            Log.d("drinkId", drinkId.toString())

            //api연결
            if (drinkId != null) {
                setDrinkDetail(drinkId)
            }
        }

        binding.buttonInterest.setOnClickListener {
            isInterest = !isInterest
            updateInterest(isInterest)
        }



        binding.drinkDetailReviewWritingBtn.setOnClickListener {
            val intent = Intent(this, ReviewWritingActivity::class.java)
            startActivity(intent)
        }

        binding.reviewMoreCl.setOnClickListener {
            val intent = Intent(this, DrinkDetailReviewsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonBack.setOnClickListener{
            finish()

            val fragmentManager: FragmentManager = supportFragmentManager
            fragmentManager.popBackStack()
        }
    }


    private fun setDrinkDetail(drinkId : Int) {
        val drinkDetailService =
            getRetrofit_header(getAccessToken(this).toString()).create(DrinkDetailApiService::class.java)
        drinkDetailService.getDrinkDetail(drinkId).enqueue(object : retrofit2.Callback<DrinkDetailResponse>{
            override fun onResponse(
                call: Call<DrinkDetailResponse>,
                response: Response<DrinkDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val resp = response.body()

                    Log.i("GETDRINKDETAIL/SUCCESS", resp.toString())

                    val nameKr = resp!!.result.nameKr
                    binding.mainNameTv.text = nameKr

                    val totalRating = resp.result.totalRating
                    val rating = String.format("%.2f", totalRating)
                    binding.drinkDetailToalRatingTv.text = rating.toString()
                    updateRating(totalRating)

                    val reviewCount = resp.result.reviewCount
                    binding.drinkDetailReviewCountTv.text = reviewCount.toString()
                    val alcohol = resp.result.alcohol
                    binding.alcoholPercentageTv.text = alcohol.toString()
                    val description = resp.result.description
                    binding.detailInformationTv.text = description
                    val manufacturer = resp.result.manufacturer
                    binding.beerCategory.text = manufacturer

                    val imageUrl = resp.result.drinkImageUrlList[0]
                    Glide.with(this@DrinkDetailActivity)
                        .load(imageUrl)
                        .placeholder(R.drawable.drink_detail_main_image)
                        .error(R.drawable.drink_detail_main_image)
                        .fallback(R.drawable.drink_detail_main_image)
                        .into(binding.mainImageIv)

                    val isliked = resp.result.liked
                    updateInterest(isliked)

                    val reviews = resp.result.reviewPreviews
                    reviewAdapter = ReviewAdapter(reviews)
                    binding.reviewRv.adapter = reviewAdapter
                    binding.reviewRv.layoutManager = LinearLayoutManager(this@DrinkDetailActivity, LinearLayoutManager.HORIZONTAL, false)

                }
            }
            override fun onFailure(call: Call<DrinkDetailResponse>, t: Throwable) {
                Log.i("GETDRINKDETAIL/FAILURE", t.message.toString())
            }

            })

        }

    fun updateInterest(state : Boolean) {
        if (state) { // 찜하기 on 상태
            binding.interestOn.visibility= View.VISIBLE
            binding.interestOff.visibility= View.GONE
        }else {
            binding.interestOn.visibility= View.GONE
            binding.interestOff.visibility= View.VISIBLE
        }
    }

    fun updateRating(rating : Float){
        if(rating == 0.0f){
            //
        } else if(rating > 0.0f && rating < 1.0f){
            binding.star1Half.visibility = View.VISIBLE
        }else if(rating == 1.0f || rating > 1.0f) {
            binding.star1Full.visibility = View.VISIBLE
        }
        // 누적
        if(rating > 1.0f && rating < 2.0f){
            binding.star2Half.visibility = View.VISIBLE
        }else if(rating == 2.0f || rating > 2.0f){
            binding.star2Full.visibility = View.VISIBLE
        }

        if(rating > 2.0f && rating < 3.0f){
            binding.star3Half.visibility = View.VISIBLE
        }else if(rating == 3.0f || rating > 3.0f){
            binding.star3Full.visibility = View.VISIBLE
        }

        if(rating > 3.0f && rating < 4.0f){
            binding.star4Half.visibility = View.VISIBLE
        }else if(rating == 4.0f || rating > 4.0f){
            binding.star4Full.visibility = View.VISIBLE
        }

        if(rating > 4.0f && rating < 5.0f){
            binding.star5Half.visibility = View.VISIBLE
        }else if(rating == 5.0f || rating > 5.0f){
            binding.star5Full.visibility = View.VISIBLE
        }

    }
}
