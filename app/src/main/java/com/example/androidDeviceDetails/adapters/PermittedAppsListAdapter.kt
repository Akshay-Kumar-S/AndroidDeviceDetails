package com.example.androidDeviceDetails.adapters
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.androidDeviceDetails.R
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppInfoItemViewHolder
import com.example.androidDeviceDetails.models.permissionsModel.PermittedAppList
import com.example.androidDeviceDetails.utils.Utils

class PermittedAppsListAdapter(
    private var _context: Context,
    private var resource: Int,
    private var items: List<PermittedAppList>
) : ArrayAdapter<PermittedAppList>(_context, resource, items) {

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }

    @SuppressLint("InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(_context)
        var vi = convertView

            var holder: PermittedAppInfoItemViewHolder?
            if (convertView == null) {
                vi = layoutInflater.inflate(resource, null)
                holder = PermittedAppInfoItemViewHolder(
                    vi.findViewById(R.id.appName),
                    vi.findViewById(R.id.appVersionCode),
                    vi.findViewById(R.id.appIcon)
                )
                vi.tag = holder
            } else holder = vi?.tag as PermittedAppInfoItemViewHolder
            holder = setAppInfoHolder(holder,position)

        return vi!!
    }

    @SuppressLint("SetTextI18n")
    private fun setAppInfoHolder(holder: PermittedAppInfoItemViewHolder, position: Int): PermittedAppInfoItemViewHolder{
        holder.appNameView.text = items[position].apk_title
        holder.versionCodeTextView.text =
            "Version Code : " + items[position].version_name
        holder.appIconView.setImageDrawable(Utils.getApplicationIcon(items[position].package_name))
        return holder
    }

}