package dtuqac.runtimerapp;

import android.content.Context;
import android.support.v7.widget.VectorEnabledTintResources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.List;
import java.util.Objects;
import java.util.jar.Attributes;

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
        return null;
    }

    public SplitDefinition GetDefDataAt(int pos)
    {
        return DefData.get(pos);
    }

    public Split GetTimeDataAt(int pos)
    {
        return TimeData.get(pos);
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
        TextView NameText = (TextView) vi.findViewById(R.id.txtSplitName);
        TextView TimeText = (TextView) vi.findViewById(R.id.txtSplitTime);
        NameText.setText(DefData.get(position).getSplitName());
        TimeText.setText(TimeData.get(position).getSplitTime().getStringWithoutZero());


/*


        NameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    final EditText NameText = (EditText) v;
                    String temp = ((EditText) v).getText().toString();
                    DefData.get(pos).setSplitName(temp);
                    Toast.makeText(v.getContext(),"NoFocus " + pos,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(v.getContext(),"Focus " + pos,Toast.LENGTH_SHORT).show();
                }
            }
        });

        TimeText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    Toast.makeText(v.getContext(),"NoFocus " + pos,Toast.LENGTH_SHORT).show();
                    //final EditText TimeText = (EditText) v;
                    //String temp = ((EditText) v).getText().toString();
                }
                else
                {
                    Toast.makeText(v.getContext(),"Focus " + pos,Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        return vi;
    }
}
