package com.apps.magicleon.stargazers.models
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class Stargazer(
    @SerializedName("id")
    val Id: String,

    @SerializedName("avatar_url")
    val AvatarURL: String,

    @SerializedName("login")
    val Name: String,

    @SerializedName("html_url")
    val ProfileURL: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Id)
        parcel.writeString(AvatarURL)
        parcel.writeString(Name)
        parcel.writeString(ProfileURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Stargazer> {
        override fun createFromParcel(parcel: Parcel): Stargazer {
            return Stargazer(parcel)
        }

        override fun newArray(size: Int): Array<Stargazer?> {
            return arrayOfNulls(size)
        }
    }
}