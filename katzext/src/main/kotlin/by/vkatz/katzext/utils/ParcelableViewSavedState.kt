package by.vkatz.katzext.utils

import android.os.Parcel
import android.os.Parcelable
import android.view.AbsSavedState

class ParcelableViewSavedState : AbsSavedState {
    val key: String
    val state: Parcelable

    constructor(key: String, state: Parcelable, superState: Parcelable) : super(superState) {
        this.key = key
        this.state = state
    }

    constructor(parcel: Parcel) : super(parcel) {
        key = parcel.readString()
        state = parcel.readParcelable(ClassLoader.getSystemClassLoader())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(key)
        parcel.writeParcelable(state, 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableViewSavedState> {
        override fun createFromParcel(parcel: Parcel): ParcelableViewSavedState {
            return ParcelableViewSavedState(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableViewSavedState?> {
            return arrayOfNulls(size)
        }
    }
}