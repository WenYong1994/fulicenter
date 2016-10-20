package cn.ucai.fulicenter.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryListActivity;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.application.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.CommonUtils;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.MFGT;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends BaseFragment {


    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<ArrayList<CategoryChildBean>> mGhildList;

    @Bind(R.id.category_expandable_listView)
    ExpandableListView categoryExpandableListView;

    CategoryExpandAdpter mAdapter;
    //用来判断是否在拖动中
    int newState;

    //定义一个全局变量来保存expandableListView的哪个posotion被打开
    int whoExpand;
    boolean isExpand=false;


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
        super.onCreateView(inflater,container,savedInstanceState);
//        downData();
//        initView();
        setListener();
        return view;
    }

    private void setListener() {
        categoryExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                newState=scrollState;
                //Toast.makeText(FuLiCenterApplication.getInstance(), "开始拖动", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(isExpand){
                    //先通过那个被打开，来获取。被展开的展开项里有多少个数据。来判断展开项的最后一再是在第几个。
                    int isExpandChildrenCount = mAdapter.getChildrenCount(whoExpand);
                    int childrenLastPosition = isExpandChildrenCount+whoExpand;
                    //这就表示显示到最后一个了，可以加载下一页数据了。
                    if(childrenLastPosition==view.getLastVisiblePosition()&&mAdapter.getIsMore(whoExpand)){
                        mAdapter.page_idArr[whoExpand]++;
//                        L.i("显示到最后一个了");
//                        L.i("第几个被展开:"+(whoExpand+1));
                        //L.i("第几页被加载"+mAdapter.page_idArr[whoExpand]);
                        CategoryGroupBean bean = mAdapter.getGroup(whoExpand);
                        downChildData(bean.getId(),mAdapter.page_idArr[whoExpand],whoExpand);
                    }
                   // L.i("firstVisibleItem:"+firstVisibleItem+",visibleItemCount:"+visibleItemCount+",totalItemCount+"+totalItemCount);
                }
            }
        });
    }

    public void initView() {
        mGroupList=new ArrayList<>();
        mGhildList= new ArrayList<>();
        mAdapter=new CategoryExpandAdpter(mGhildList,mGroupList,getContext());
        categoryExpandableListView.setGroupIndicator(null);
        categoryExpandableListView.setChildIndicator(null);
        categoryExpandableListView.setAdapter(mAdapter);
    }

    public void downData() {
        final OkHttpUtils<CategoryGroupBean[]> utils = new OkHttpUtils<>(getContext());
        utils.url(I.SERVER_ROOT+I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CategoryGroupBean[]>() {
                    @Override
                    public void onSuccess(CategoryGroupBean[] result) {
                        if(result!=null|result.length!=0){
                            ArrayList<CategoryGroupBean> listGroup = utils.array2List(result);
                            mAdapter.initList(null,listGroup);
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

    private void downChildData(int parent_id, int page_id, final int groupPosition){
        final OkHttpUtils<CategoryChildBean[]> utils = new OkHttpUtils<>(getContext());
        utils.url(I.SERVER_ROOT+I.REQUEST_FIND_CATEGORY_CHILDREN+"Pages")
                .addParam(I.CategoryChild.PARENT_ID,parent_id+"")
                .addParam(I.PAGE_ID,page_id+"")
                .addParam(I.PAGE_SIZE,PAGE_SIZE+"")
                .targetClass(CategoryChildBean[].class)
                .execute(new OkHttpUtils.OnCompleteListener<CategoryChildBean[]>() {
                    @Override
                    public void onSuccess(CategoryChildBean[] result) {
                        if(result!=null&&result.length!=0){
                            ArrayList<CategoryChildBean> list= utils.array2List(result);
                            mAdapter.addChildList(list,groupPosition);
                        }else {
                            //没有更多数据了
                            //Toast.makeText(FuLiCenterApplication.getInstance(), "没有跟多数据了", Toast.LENGTH_SHORT).show();
                            mAdapter.setIsMore(false,groupPosition);
                        }
                    }
                    @Override
                    public void onError(String error) {
                        CommonUtils.showShortToast("请求数据失败");
                    }
                });
    }

    class CategoryExpandAdpter extends BaseExpandableListAdapter {

        //用来保存每一个大类里面小类分页加载的页数
        int[]  page_idArr;
        //用来保存每一个大类里面的小类是否还有更多数据
        boolean[] isMore;
        ArrayList<ArrayList<CategoryChildBean>> childList;
        ArrayList<CategoryGroupBean> groupList;
        Context context;
        //带你以一个boolen数组保存这个Group是否已近被下载
        boolean[] isGroupDownArr;

        public boolean getIsMore(int groupPosition) {
            return isMore[groupPosition];
        }

        public void setIsMore(boolean is,int groupPosition) {
            this.isMore[groupPosition]=is;
        }

        public void initList(ArrayList<ArrayList<CategoryChildBean>> childList, ArrayList<CategoryGroupBean> groupList){

            //这下面是通过大类的个数，来初始化一下数据
            this.childList.clear();
            this.groupList.clear();
            this.isGroupDownArr=new boolean[groupList.size()];
            this.isMore = new boolean[groupList.size()];
            this.page_idArr=new int[groupList.size()];
            if(childList!=null){
                this.childList.addAll(childList);
            }
            if(groupList!=null){
                this.groupList.addAll(groupList);
            }
            notifyDataSetChanged();
            for (int i=0;i<groupList.size();i++){
                this.childList.add(new ArrayList<CategoryChildBean>());
                isGroupDownArr[i]=false;
                isMore[i]=true;
                page_idArr[i]=1;
            }
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

        public void addChildList(ArrayList<CategoryChildBean> list,int groupPosition){

            this.childList.get(groupPosition).addAll(list);
            notifyDataSetChanged();
        }

        public void removeChildList(int groupPosition){
            this.childList.get(groupPosition).clear();
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
            //将cat_id，和对应group的name封装到holder里面
            holder.cat_id=bean.getId();
            CategoryGroupBean beanGroup = groupList.get(groupPosition);
            holder.parentName = beanGroup.getName();
            holder.groupPosistion=groupPosition;

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
            String parentName;
            int cat_id ;
            int groupPosistion;

            ChildViewHolder(View view) {
                ButterKnife.bind(this, view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChildViewHolder holder = (ChildViewHolder) v.getTag();
                        String groupName = holder.parentName;
                        int groupPosition=holder.groupPosistion;

                        int cat_id = holder.cat_id;
                        Intent intent = new Intent();
                        intent.putExtra("groupName",groupName);
                        intent.putExtra("cat_id",cat_id);
                        intent.putExtra("groupPosition",groupPosition);
                        //Toast.makeText(FuLiCenterApplication.getInstance(), "groupName:"+groupName+"cat_id"+cat_id, Toast.LENGTH_SHORT).show();
                        MFGT.startActivity((MainActivity) context, CategoryListActivity.class,intent);
                    }
                });
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
                            isExpand=false;
                        }else {
                            //展开此项
                            categoryExpandableListView.expandGroup(position);
                            CategoryGroupBean bean = groupList.get(position);
                            whoExpand=position;
                            isExpand=true;
                            //这里判断以前是否已近加载过了，如果是已近加载过了，就不用再下载
                            if(!isGroupDownArr[position]){
                                isGroupDownArr[position]=true;
                                downChildData(bean.getId(),page_idArr[position],position);
                            }
                        }
                        //下面关闭其他项
                        for(int i=0;i<getGroupCount();i++){
                            if(i!=position){
                                //关闭此项
                                categoryExpandableListView.collapseGroup(i);
                            }
                        }
                        //Toast.makeText(FuLiCenterApplication.getInstance(),"position:"+position+",name:"+getGroup(position).getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /*//在CategoryFragment里面定义一个方法关闭展开项
    public void unExpand(int whoExpand){
        if(whoExpand<0){
            try {
                throw new Exception("没有此项被打开");
            } catch (Exception e) {
                L.e(e.getMessage());
            }
            return;
        }
        categoryExpandableListView.collapseGroup(whoExpand);
    }*/

    @Override
    public void onResume() {
        super.onResume();
        for(int i=0;i<mAdapter.getGroupCount();i++){
            categoryExpandableListView.collapseGroup(i);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


}
