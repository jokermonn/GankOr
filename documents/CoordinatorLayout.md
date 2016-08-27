原文地址：https://guides.codepath.com/android/Handling-Scrolls-with-CoordinatorLayout#troubleshooting-coordinated-layouts
## 预览 ##
CoordinatorLayout 扩展了许多能实现 Google Material Design [scrolling effects](https://material.google.com/patterns/scrolling-techniques.html)（滚动技巧） 的效果，目前，这个框架内提供了几种方法使它并不需要自定义动画就可以运行，有以下几种效果：

- 上下移动 Floating Action Button 以便给 Snackbar 腾出空间
- 扩张或收缩 Toolbar 或者顶部空间以便给 main content 腾出空间
- 控制某个 view 可以扩张或收缩，以及扩张或收缩的速率大小，包括 [parallax scrolling effects](https://ihatetomatoes.net/demos/parallax-scroll-effect/) 动画

## 代码示例 ##
来自 Google 的 Chris Banes 将 ``CoordinatorLayout`` 和其它 [design support library](https://guides.codepath.com/android/Design-Support-Library) 特征融合在一起做的一个 demo，[源代码](https://github.com/chrisbanes/cheesesquare)可从 github 上查找，这个项目是让你理解 ``CoordinatorLayout`` 最好的路线之一

## 使用 ##
确定遵循 [Design Support Library](https://guides.codepath.com/android/Design-Support-Library) 使用说明

## Floating Action Buttons and Snackbars ##

## 扩展或收缩 Toolbars ##
第一步确定你没有使用过时的那个 ActionBar。第二步确定遵循 [Using the ToolBar as ActionBar](https://guides.codepath.com/android/Using-the-App-ToolBar#using-toolbar-as-actionbar) 说明。第三步确定 CoordinatorLayout 是布局文件的容器

	<android.support.design.widget.CoordinatorLayout 
		xmlns:android="http://schemas.android.com/apk/res/android"
		xmlns:app="http://schemas.android.com/apk/res-auto"
		android:id="@+id/main_content"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:fitsSystemWindows="true">
	
	      <android.support.v7.widget.Toolbar
				android:id="@+id/toolbar"
	            android:layout_width="match_parent"
	            android:layout_height="?attr/actionBarSize"
	            app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
	
	</android.support.design.widget.CoordinatorLayout>

## 对滑动事件的响应 ##
接下来，我们要让 ToolBar 对滑动事件作出响应，我们使用 [AppBarLayout](https://developer.android.com/reference/android/support/design/widget/AppBarLayout.html) 来作为容器：

	<android.support.design.widget.AppBarLayout
	        android:id="@+id/appbar"
	        android:layout_width="match_parent"
	        android:layout_height="@dimen/detail_backdrop_height"
	        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
	        android:fitsSystemWindows="true">
	
		  <android.support.v7.widget.Toolbar
		          android:id="@+id/toolbar"
		          android:layout_width="match_parent"
		          android:layout_height="?attr/actionBarSize"
		          app:popupTheme="@style/ThemeOverlay.AppCompat.Light" />
	
	 </android.support.design.widget.AppBarLayout>

注意：根据 [Goolge 官方文档](https://developer.android.com/reference/android/support/design/widget/AppBarLayout.html)，官方希望 AppBarLayout 作为第一个子 view 嵌套在 CoordinatorLayout 中。
然后，我们在 AppBarLayout 和 你所想要滑动的 View 之间建立一个联系。 给 RecyclerView 或者其它例如 [NestedScrollView](http://stackoverflow.com/questions/25136481/what-are-the-new-nested-scrolling-apis-for-android-l) 一样能够使用滑动的 View 添加一个属性 ``app:layout_behavior``。support library 中包含了一个特别的字符串资源 ``@string/appbar_scrolling_view_behavior``，它映射自 [AppBarLayout.ScrollingViewBehavior](https://developer.android.com/reference/android/support/design/widget/AppBarLayout.ScrollingViewBehavior.html)，当指定视图的滑动事件发生了，它就用来通知 ``AppBarLayout``。这个 behavior 属性必须添加在触发滑动事件的那个 view 上。

	<android.support.v7.widget.RecyclerView
	        android:id="@+id/rvToDoList"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent"
	        app:layout_behavior="@string/appbar_scrolling_view_behavior"/>

当 CoordinatorLayout 发现 RecyclerView 中声明了这个属性的话，它就会查询其他的 views，找到任何与这个 behavior 有关的 views，在这个例子当中，``AppBarLayout.ScrollingViewBehavior`` 描述了 RecyclerView 和 AppBarLayout 之间的依赖关系，RecyclerView 的任何滑动事件都会触发 AppBarLayout 或者其它的包含这个属性的 views的改变。

通过使用 ``app:layout_scrollFlags`` 属性，RecyclerView 的滑动事件会改变 AppBarLayout 的子 views：

	<android.support.design.widget.AppBarLayout
	        android:layout_width="match_parent"
	        android:layout_height="wrap_content"
	        android:fitsSystemWindows="true"
	        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar">
	
	            <android.support.v7.widget.Toolbar
	                android:id="@+id/toolbar"
	                android:layout_width="match_parent"
	                android:layout_height="?attr/actionBarSize"
	                app:layout_scrollFlags="scroll|enterAlways"/>
	
	 </android.support.design.widget.AppBarLayout>

。。。。。。这个 flag 必须与 ``enterAlways``，``enterAlwaysCollapsed``，``exitUntiCollapsed``或者 ``snap`` 一起使用：

- ``enterAlways``：界面向上滑动的时候，这个 view 就变得可见。当当前 list 滑动到底部然后想让 ``Toolbar`` 在界面上划的时候就能出来的时候，这个 flag 是非常有用的。通常情况下，我们希望 ``Toolbar`` 只会在 list 滑动到顶部的时候出现。
- ``enterAlwaysCollapsed``：通常情况下，如果仅仅使用 ``enterAlways``，只要你向上滑动界面，``Toolbar`` 就会不停扩展。假设当前我们已经声明了 ``enterAlways`` 属性，并且我们定义了一个 ``minHeight``，那么你也可以指定 ``enterAlwaysCollapsed``，当这个设置被使用的时候，那么在向上滑动界面的过程中，你的 view 会一直以 ``minHeight`` 的高度显示在屏幕上，并且在到达顶端的时候继续滑动的话，它将扩展到它的最大高度。
- ``exitUntilCollapsed``：当 ``scroll`` 属性被设置了，我们向下滑动界面的时候，界面中全部的内容都会跟着滑动。通过定义 ``minHeight`` 和 ``exitUntilCollapsed``，在 ``Toolbar`` 到达最小的高度的时候，它会一直停留在屏幕上端
- ``snap``：使用这个操作可以定义当一个 view 局部减少时候的响应。假如滑动结束的时候，当前 view 有小于一半的内容被滑动到屏幕外面去了，那么就恢复成最开始的样子，如果有大于一半的内容被滑动到屏幕外，那么滑动结束的时候，这个 view 就会全部消失。

注意：一定要记得设置 ``scroll``，这样的话，在顶部的被折叠的 view 才会首先退出。

至此，你应该理解了 Toolbar 对滑动事件的响应。 

## 构建折叠效果 ##
如果我们想做一个 Toolbar 的折叠效果，我们必须要用 CollapsingToolbarLayout 来作为 Toolbar 的父布局：

	<android.support.design.widget.CollapsingToolbarLayout
	            android:id="@+id/collapsing_toolbar"
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:fitsSystemWindows="true"
	            app:contentScrim="?attr/colorPrimary"
	            app:expandedTitleMarginEnd="64dp"
	            app:expandedTitleMarginStart="48dp"
	            app:layout_scrollFlags="scroll|exitUntilCollapsed"/>
	            
	            <android.support.v7.widget.Toolbar
	                android:id="@+id/toolbar"
	                android:layout_width="match_parent"
	                android:layout_height="?attr/actionBarSize"
	                app:layout_scrollFlags="scroll|enterAlways"></android.support.v7.widget.Toolbar>
	
	</android.support.design.widget.CollapsingToolbarLayout>

效果如下图所示：

一般情况下，我们需要给 Toolbar 设置标题，现在我们给 CollapsingToolBarLayout 设置标题而不是 Toolbar。

	CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
	 collapsingToolbar.setTitle("Title");

注意当使用 ``CollapsingToolbarLayout`` 时，系统状态栏应该设置成半透明（API 19）的或透明的（API 21），就像[这个文件](https://github.com/chrisbanes/cheesesquare/blob/master/app/src/main/res/values-v21/styles.xml) 。特别地，下面的样式应该写在 ``res/values-xx/styles.xml`` 中以示说明：

	<!-- res/values-v19/styles.xml -->
	<style name="AppTheme" parent="Base.AppTheme">
	    <item name="android:windowTranslucentStatus">true</item>
	</style>
	
	<!-- res/values-v21/styles.xml -->
	<style name="AppTheme" parent="Base.AppTheme">
	    <item name="android:windowDrawsSystemBarBackgrounds">true</item>
	    <item name="android:statusBarColor">@android:color/transparent</item>
	</style>

通过使用上面的半透明系统状态栏，你的布局应该填充了系统状态栏以外的区域，所以你应该声明 ``android:fitsSystemWindow`` 来让系统状态栏不要覆盖了属于你布局的那部分。对于 API 19，还有一种方法就是使用 ``padding`` 来避免系统状态栏遮挡住 views，可以参考[这篇文章](http://blog.raffaeu.com/archive/2015/04/11/android-and-the-transparent-status-bar.aspx)。

## 构建视差动画 ##
CollapsingToolbarLayout 也可以让我们使用更多的高级动画，构建 ImageView 在折叠过程中渐出的效果。标题栏也是同样可以随着滑动改变的。

为了做出这种效果，我们添加一个 ImageView 并且添加一个 ``app:layout_collapseMode="parallax"`` 属性。


	<android.support.design.widget.CollapsingToolbarLayout
	    android:id="@+id/collapsing_toolbar"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:fitsSystemWindows="true"
	    app:contentScrim="?attr/colorPrimary"
	    app:expandedTitleMarginEnd="64dp"
	    app:expandedTitleMarginStart="48dp"
	    app:layout_scrollFlags="scroll|exitUntilCollapsed">
	
	            <android.support.v7.widget.Toolbar
	                android:id="@+id/toolbar"
	                android:layout_width="match_parent"
	                android:layout_height="?attr/actionBarSize"
	                app:layout_scrollFlags="scroll|enterAlways"></android.support.v7.widget.Toolbar>
	            <ImageView
	                android:src="@drawable/cheese_1"
	                app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed"
	                android:layout_width="wrap_content"
	                android:layout_height="wrap_content"
	                android:scaleType="centerCrop"
	                app:layout_collapseMode="parallax"
	                android:minHeight="100dp"/>
	
	</android.support.design.widget.CollapsingToolbarLayout>

## 底部动作条 ##
现在在 support design library v23.2 版本中支持底部动作条，当前底部动作条支持两种类型： [persistent](https://material.google.com/components/bottom-sheets.html#bottom-sheets-persistent-bottom-sheets) 和 [modal](https://material.google.com/components/bottom-sheets.html#bottom-sheets-modal-bottom-sheets) 。persistent 底部动作条是显示 App 内部的内容，而 modal 底部动作条则是显式菜单或者一个简单的 dialogs。

### 持久性形式动作条（Persistent Modal Sheets） ###
有两种方法可以创建持久性形式动作条。第一种方法是使用 NestedScrollView，只是简单地在该视图中嵌入内容。第二种方法就是使用一个 [RecyclerView](https://guides.codepath.com/android/Using-the-RecyclerView) 嵌套在一个 CoordinatorLayout 中。如果这个 ``RecyclerView`` 的 ``layout_behavior`` 的值是预定义的 ``@string/bottom_sheet_behavior`` 那么默认情况下 ``RecyclerView`` 是隐藏的。同时需要注意的是，``RecyclerView`` 应该使用 ``wrap_content`` 而不是 ``match_parent``，这样就可以允许底部动作条只会占用所需要的地方而不是整个页面：

	<CoordinatorLayout>
	
	    <android.support.v7.widget.RecyclerView
	        android:id="@+id/design_bottom_sheet"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content"
	        app:layout_behavior="@string/bottom_sheet_behavior">
	</CoordinatorLayout>

后面一步就是创建 ``RecyclerView`` 元素。我们使用一个简单的包含图片和文字的 ``Item``，还有一个能引入这些 items 的适配器。

	public class Item {
	
	    private int mDrawableRes;
	
	    private String mTitle;
	
	    public Item(@DrawableRes int drawable, String title) {
	        mDrawableRes = drawable;
	        mTitle = title;
	    }
	
	    public int getDrawableResource() {
	        return mDrawableRes;
	    }
	
	    public String getTitle() {
	        return mTitle;
	    }
	
	}

然后我们创建适配器：

	public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
	
	    private List<Item> mItems;
	
	    public ItemAdapter(List<Item> items, ItemListener listener) {
	        mItems = items;
	        mListener = listener;
	    }
	
	    public void setListener(ItemListener listener) {
	        mListener = listener;
	    }
	
	    @Override
	    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	        return new ViewHolder(LayoutInflater.from(parent.getContext())
	                .inflate(R.layout.adapter, parent, false));
	    }
	
	    @Override
	    public void onBindViewHolder(ViewHolder holder, int position) {
	        holder.setData(mItems.get(position));
	    }
	
	    @Override
	    public int getItemCount() {
	        return mItems.size();
	    }
	
	    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
	
	        public ImageView imageView;
	        public TextView textView;
	        public Item item;
	
	        public ViewHolder(View itemView) {
	            super(itemView);
	            itemView.setOnClickListener(this);
	            imageView = (ImageView) itemView.findViewById(R.id.imageView);
	            textView = (TextView) itemView.findViewById(R.id.textView);
	        }
	
	        public void setData(Item item) {
	            this.item = item;
	            imageView.setImageResource(item.getDrawableResource());
	            textView.setText(item.getTitle());
	        }
	
	        @Override
	        public void onClick(View v) {
	            if (mListener != null) {
	                mListener.onItemClick(item);
	            }
	        }
	    }
	
	    public interface ItemListener {
	        void onItemClick(Item item);
	    }
	}

底部动作条在默认情况下应该是隐藏的。我们需要通过一个点击事件来触发它显示或者隐藏。注意：不要在 ``onCreate()`` 方法中试图去扩展底部动作条，原因[在此](https://code.google.com/p/android/issues/detail?id=202174) 。

	RecyclerView recyclerView = (RecyclerView) findViewById(R.id.design_bottom_sheet); 
	
	// Create your items
	ArrayList<Item> items = new ArrayList<>();
	items.add(new Item(R.drawable.cheese_1, "Cheese 1"));
	items.add(new Item(R.drawable.cheese_2, "Cheese 2"));
	
	// Instantiate adapter
	ItemAdapter itemAdapter = new ItemAdapter(items, null);
	recyclerView.setAdapter(itemAdapter);
	
	// Set the layout manager
	recyclerView.setLayoutManager(new LinearLayoutManager(this));
	
	CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
	final BottomSheetBehavior behavior = BottomSheetBehavior.from(recyclerView);
	
	fab.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View view) {
	       if(behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
	         behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
	       } else {
	         behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
	       }
	    }
	});

你也可以设置一个布局属性 ``app:behavior_hideable="true"`` 来允许用户可以通过滑动底部动作条来让其消失。还有其他的状态包括 ``STATE_DRAGGING``，``STATE_SETTLING``，还有 ``STATE_HIDDEN``。更多详情，可以看一下另一篇[底部动作条教程](http://code.tutsplus.com/articles/how-to-use-bottom-sheets-with-the-design-support-library--cms-26031) 。

### 形式动作条 ###

形式动作条实际上就是一个 Dialog Fragments（?）。看这篇关于如何创建这些类型的 fragments 的[向导](https://guides.codepath.com/android/Using-DialogFragment) 。而不是从 ``DialogFragment`` 扩展，你应该从 ``BottomSheetDialogFragment`` 扩展。

### CoordinatedLayout 的故障排除 ###

``CoordinatedLayout`` 是非常有用但是最开始使用的时候也是很容易出错的。如果你在运行中遇到了协调行为的问题，可以参考一下以下提示：
- 关于如何最有效地使用 CoordinatedLayout 的例子就是认真地参考 [cheesesquare 的源码 ](https://github.com/chrisbanes/cheesesquare) 。这个仓库的例子被 Google 一直在维护的，它展现了协调行为的最好使用。特别地，可以看一下[选项卡式 ViewPager 列表的布局](https://github.com/chrisbanes/cheesesquare/blob/master/app/src/main/res/layout/include_list_viewpager.xml) 和  [this for a layout for a detail view](https://github.com/chrisbanes/cheesesquare/blob/master/app/src/main/res/layout/activity_detail.xml) 。小心翼翼地比较你的代码和 cheesesquare 源码的区别。
- 当协调一个 ``ViewPager`` 和一个父 activity 中含一个带有 list 的fragment 时，