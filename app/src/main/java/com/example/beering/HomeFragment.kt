package com.example.beering
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beering.api.*
import com.example.beering.data.getAccessToken
import com.example.beering.databinding.FragmentHomeBinding
import com.example.naverwebtoon.data.DrinkCover
import retrofit2.Call
import retrofit2.Response

class HomeFragment: Fragment() {
    lateinit var binding:FragmentHomeBinding
    lateinit var homeAdapter: HomeAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val recyclerView: RecyclerView = binding.homePostRv

        // api 연결
        val homeService =
            getRetrofit_header(getAccessToken(requireContext()).toString()).create(ReviewsApiService::class.java)
        homeService.getReviews().enqueue(object : retrofit2.Callback<ReviewsResponse>{
            override fun onResponse(
                call: Call<ReviewsResponse>, response: Response<ReviewsResponse>
            ) {
                val resp = response.body()
                if(resp!!.isSuccess) {
                    val reviews = resp.result.content
                    homeAdapter = HomeAdapter(reviews)

                    homeAdapter!!.setOnItemClickListener(object :
                        HomeAdapter.OnItemClickListener {
                        override fun onItemClick(review: ReviewsContent) {
                            // 리뷰 상세보기 페이지
                            val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
                            intent.putExtra("reviewId", review.reviewId)
                            startActivity(intent)
                        }
                    })


                    homeAdapter!!.setOnLikeClickListener(object:HomeAdapter.OnLikeClickListener {
                        override fun onButtonClick(position: Int) {
                            homeAdapter!!.notifyItemChanged(position, "likeChange")
                        }
                    })

                    recyclerView.adapter = homeAdapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())


                }else {
                    if(resp.responseCode == 2003) token(requireContext())
                }
            }

            override fun onFailure(call: Call<ReviewsResponse>, t: Throwable) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("요청 오류")
                builder.setMessage("서버에 요청을 실패하였습니다.")
                builder.setPositiveButton("네") { dialog, which ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }

        })

        //삭제해야 함.
        binding.itemHomePostProfileCv.setOnClickListener {
            val intent = Intent(requireContext(), ReviewDetailActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

}