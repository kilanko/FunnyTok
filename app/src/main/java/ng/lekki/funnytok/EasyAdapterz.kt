package ng.lekki.funnytok


import android.support.annotation.LayoutRes
import com.shprot.easy.adapter.EasyAdapter
import com.shprot.easy.adapter.EasyViewHolder

class EasyAdapterz(
        @LayoutRes
        itemLayoutRes: Int,
        itemCount: Int,
        binder: (EasyViewHolder) -> Unit = {}
) : EasyAdapter(itemLayoutRes,itemCount, binder)