package backend.team.ahachul_backend.api.lost.domain.model

import com.google.gson.annotations.SerializedName


data class Lost112Data(
    @SerializedName("ID")
    val id: String = "",
    val title: String = "",
    val getDate: String = "",
    val getPlace: String = "",
    @SerializedName("type")
    val categoryName: String = "",
    val receiptPlace: String = "",
    val storagePlace: String = "",
    val lostStatus: String = "",
    val phone: String = "",
    val context: String = "",
    @SerializedName("image")
    val imageUrl: String = "",
    @SerializedName("source")
    val origin: String = "",
    val page: String = ""
)
