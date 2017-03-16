package dtuqac.runtimerapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.List;
import java.util.Objects;

import dtuqac.runtimerapp.SpeedRunEntity;



/**
 * Created by François on 2017-03-15.
 */

public class EditSplit_Adapter extends BaseAdapter
{
    private Context context;
    private List<SplitDefinition> DefData;
    private List<Split> TimeData;
    private static LayoutInflater inflater = null;

    public EditSplit_Adapter(Context context, List<SplitDefinition> DefData, List<Split> TimeData)
    {
        this.context = context;
        this.DefData = DefData;
        this.TimeData = TimeData;
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
            vi = inflater.inflate(R.layout.edit_split_listitem,null);
        }
        EditText NameText = (EditText) vi.findViewById(R.id.txtSplitName);
        EditText TimeText = (EditText) vi.findViewById(R.id.txtSplitTime);
        NameText.setText(DefData.get(position).getSplitName());
        TimeText.setText(TimeData.get(position).getSplitTime().getStringWithoutZero());

        return vi;
    }

}
