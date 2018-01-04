package moco.htwg.de.truckparkapp.view;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import moco.htwg.de.truckparkapp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InputFreeSlotsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InputFreeSlotsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InputFreeSlotsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private final String TAG = this.getClass().getSimpleName();

    NumberPicker.OnValueChangeListener onValueChangeListener =
            new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Toast.makeText(getContext(),
                            "selected number " + numberPicker.getValue(), Toast.LENGTH_SHORT);
                    Log.d(TAG, "onValueChange: " + numberPicker.getValue());
                }
            };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private OnFragmentInteractionListener mListener;

    public InputFreeSlotsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment InputFreeSlotsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InputFreeSlotsFragment newInstance(String param1) {
        InputFreeSlotsFragment fragment = new InputFreeSlotsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_input_free_slots, container, false);

        NumberPicker np = view.findViewById(R.id.numberPicker);
        np.setMinValue(1); //at least the user is on it
        np.setMaxValue(20); //todo depending on parking spot
        np.setValue(10); //todo depending on calc usage
        np.setOnValueChangedListener(onValueChangeListener);

        Button.OnClickListener submitOnClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPicker np = view.findViewById(R.id.numberPicker);
                np.getValue(); //todo send to db
            }
        };
        Button submitButton = view.findViewById(R.id.button_submit_free_slots);
        submitButton.setOnClickListener(submitOnClickListener);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
