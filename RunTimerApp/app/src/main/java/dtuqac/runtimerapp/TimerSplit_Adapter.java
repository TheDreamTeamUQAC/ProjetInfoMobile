package dtuqac.runtimerapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by François on 2017-03-20.
 */

public class TimerSplit_Adapter extends BaseAdapter
{
    private Context context;
    private List<SplitDefinition> DefData;
    private List<Split> TimeData;
    private List<String> DeltaData;
    private int selectedItem = -1;
    private static LayoutInflater inflater = null;

    public TimerSplit_Adapter(Context context, List<SplitDefinition> DefData, List<Split> TimeData, List<String> DeltaData)
    {
        this.context = context;
        this.DefData = DefData;
        this.DeltaData = DeltaData;

        this.TimeData = new LinkedList<Split>();
        for (int i=0; i< TimeData.size(); i++)
        {
            this.TimeData.add(TimeData.get(i));
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return DefData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return DefData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        if (vi == null)
        {
            vi = inflater.inflate(R.layout.timersplit_list_item,null);
        }
        TextView NameText = (TextView) vi.findViewById(R.id.lblSplitName);
        TextView TimeText = (TextView) vi.findViewById(R.id.lblSplitTime);
        TextView DeltaText = (TextView) vi.findViewById(R.id.lblDelta);
        NameText.setText(DefData.get(position).getSplitName());
        TimeText.setText(TimeData.get(position).getSplitTime().getStringWithoutZero());
        DeltaText.setText(DeltaData.get(position));

        return vi;
    }

    private void HighlightItem(int position, View view)
    {
        if (position == selectedItem)
        {
            view.setBackgroundColor(Color.DKGRAY);
        }
        else
        {
            view.setBackground(Drawable.createFromPath("@drawable/list_selector_normal"));
        }
    }

    public void SetSelectecItem(int selectedItem)
    {
        this.selectedItem = selectedItem;
    }

    public void refreshSplits(List<Split> _TimeData, List<String> _DeltaData)
    {
        TimeData.clear();
        TimeData.addAll(_TimeData);
        DeltaData.clear();
        DeltaData.addAll(_DeltaData);
        notifyDataSetChanged();
    }
}
