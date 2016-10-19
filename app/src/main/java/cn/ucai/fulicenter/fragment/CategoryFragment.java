package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {


    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mGhildList;

    @Bind(R.id.category_expandable_listView)
    ExpandableListView categoryExpandableListView;

    CategoryExpandAdpter mAdapter;

    final int PAGE_SIZE= 10;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        downData();
        initView();
        return view;
    }

    private void initView() {
        mGroupList=new ArrayList<>();
        mGhildList= new ArrayList<>();
        mAdapter=new CategoryExpandAdpter(mGhildList,mGroupList,getContext());
        categoryExpandableListView.setGroupIndicator(null);
        categoryExpandableListView.setChildIndicator(null);
        categoryExpandableListView.setAdapter(mAdapter);
    }

    private void downData() {
        final OkHttpUtils<CategoryGroupBean[]> utils = new OkHttpUtils<>(getContext());
        utils.url(I.SERVER_ROOT+I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CategoryGroupBean[]>() {
                    @Override
                    public void onSuccess(CategoryGroupBean[] result) {
                        if(result!=null|result.length!=0){
                            ArrayList<CategoryGroupBean> listGroup = utils.array2List(result);
                            mAdapter.initList(null,listGroup);
                            for(CategoryGroupBean bean: listGroup){

                            }
                        }else {
                            Toast.makeText(FuLiCenterApplication.getInstance(), "加载数据失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        L.e(error);
                    }
                });
    }

    private void downChildData(String uri,int page_id){

    }


    class CategoryExpandAdpter extends BaseExpandableListAdapter {

        ArrayList<ArrayList<CategoryChildBean>> childList;
        ArrayList<CategoryGroupBean> groupList;
        Context context;

        public void initList(ArrayList<ArrayList<CategoryChildBean>> childList,ArrayList<CategoryGroupBean> groupList){

            this.childList.clear();
            this.groupList.clear();
            if(childList!=null){
                this.childList.addAll(childList);
            }
            if(groupList!=null){
                this.groupList.addAll(groupList);
            }
            notifyDataSetChanged();
        }

        public CategoryExpandAdpter(ArrayList<ArrayList<CategoryChildBean>> childList, ArrayList<CategoryGroupBean> groupList, Context context) {
            this.childList = childList;
            this.groupList = groupList;
            this.context = context;
        }

        @Override
        public int getGroupCount() {
            return groupList == null ? 0 : groupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            return (childList == null||childList.size()==0||childList.get(groupPosition)==null
                    ||childList.get(groupPosition).size()==0) ? 0 : childList.get(groupPosition).size();
        }

        @Override
        public CategoryGroupBean getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public CategoryChildBean getChild(int groupPosition, int childPosition) {
            return childList.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            CategoryGroupBean bean = groupList.get(groupPosition);
            GroupViewHolder holder=null;
            if(convertView==null){
                convertView = View.inflate(context,R.layout.category_group_item,null);
                holder= new GroupViewHolder(convertView,groupPosition);
                convertView.setTag(holder);
            }else {
                holder  = (GroupViewHolder) convertView.getTag();
            }
            holder.position=groupPosition;
            holder.categoryGroupText.setText(bean.getName());
            Picasso.with(context)
                    .load(I.DOWNLOAD_IMG_URL+bean.getImageUrl())
                    .error(R.drawable.nopic)
                    .placeholder(R.drawable.nopic)
                    .into(holder.categoryGroupImageView);
            if(isExpanded){
                holder.categoryGroupArrow.setImageResource(R.mipmap.expand_off);
            }else {
                holder.categoryGroupArrow.setImageResource(R.mipmap.expand_on);
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder =null;
            CategoryChildBean bean = childList.get(groupPosition).get(childPosition);
            if(convertView==null){
                convertView = View.inflate(context,R.layout.category_child_item,null);
                holder  = new ChildViewHolder(convertView);
                convertView.setTag(holder);
            }else {
                holder= (ChildViewHolder) convertView.getTag();
            }

            holder.categoryChildText.setText(bean.getName());
            Picasso.with(context)
                    .load(I.DOWNLOAD_IMG_URL+bean.getImageUrl())
                    .error(R.drawable.nopic)
                    .placeholder(R.drawable.nopic)
                    .into(holder.categoryChildImage);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
        //定义两个封装类，用来优化ListView
        class ChildViewHolder {
            @Bind(R.id.category_child_image)
            ImageView categoryChildImage;
            @Bind(R.id.category_child_text)
            TextView categoryChildText;
            ChildViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        class GroupViewHolder {
            @Bind(R.id.category_group_imageView)
            ImageView categoryGroupImageView;
            @Bind(R.id.category_group_text)
            TextView categoryGroupText;
            @Bind(R.id.category_group_arrow)
            ImageView categoryGroupArrow;
            private int position;

            public int getPosition() {
                return position;
            }

            public void setPosition(int position) {
                this.position = position;
            }

            GroupViewHolder(View view, int position) {
                this.position=position;
                ButterKnife.bind(this, view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupViewHolder holer  = (GroupViewHolder) v.getTag();
                        int position = holer.position;
                        if(categoryExpandableListView.isGroupExpanded(position)){
                            //关闭此项
                            categoryExpandableListView.collapseGroup(position);
                            L.i("关闭此项");
                        }else {
                            //展开此项
                            categoryExpandableListView.expandGroup(position);
                            L.e("展开此项");
                        }
                        //下面关闭其他项
                        for(int i=1;i<=getGroupCount();i++){
                            if(i!=position){
                                //关闭此项
                                categoryExpandableListView.collapseGroup(position);
                            }
                        }
                        Toast.makeText(FuLiCenterApplication.getInstance(),"position:"+position+",name:"+getGroup(position).getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
