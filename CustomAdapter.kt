package com.dam.e_biblioteka
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import com.dam.e_biblioteka.ItemsViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dam.e_biblioteka.R.*

class CustomAdapter(private val mList: List<ItemsViewModel>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    public val data=mList;
    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener?=null
    private lateinit var holder:ViewHolder;
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        this.holder=holder;

        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.textView.setEllipsize(TextUtils.TruncateAt.MIDDLE)
        holder.textView.text = ItemsViewModel.text

        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position, ItemsViewModel )
            }
        }

        holder.itemView.setOnLongClickListener{
            onLongClickListener!!.onLongClick(position,ItemsViewModel)

        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun setCardBgColor(color:Int)
    {
        holder.itemView.setBackgroundColor(Color.CYAN);
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOnLongClickListener(onLongClickListener: OnLongClickListener)
    {
        this.onLongClickListener=onLongClickListener;
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: ItemsViewModel)
    }

    interface OnLongClickListener{
        fun onLongClick(position:Int,model:ItemsViewModel):Boolean
        {
            return true;
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(id.imgBook)
        val textView: TextView = itemView.findViewById(id.textView);
        val cardView:View = itemView;
    }
}