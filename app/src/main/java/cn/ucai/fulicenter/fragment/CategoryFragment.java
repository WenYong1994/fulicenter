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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {


    ArrayList<CategoryGroupBean> mGroupList;
    ArrayList<CategoryChildBean> mGhildList;

    @Bind(R.id.category_expandable_listView)
    ExpandableListView categoryExpandableListView;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    class CategoryExpandAdpter extends BaseExpandableListAdapter {

        ArrayList<ArrayList<CategoryChildBean>> childList;
        ArrayList<CategoryGroupBean> groupList;
        Context context;

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
            return childList == null ? 0 : childList.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
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
            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
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

            GroupViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }



    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
