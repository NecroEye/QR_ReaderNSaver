package com.muratcangzm.qrreader.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.muratcangzm.qrreader.R;
import com.muratcangzm.qrreader.RecyclerView.Adapter;
import com.muratcangzm.qrreader.RecyclerView.RecyclerModel;
import com.muratcangzm.qrreader.RecyclerView.RecyclerViewEventListener;
import com.muratcangzm.qrreader.databinding.QrListBinding;

import java.util.ArrayList;
import java.util.List;

public class QRList extends Fragment implements RecyclerViewEventListener {


    private LinearLayoutManager manager;
    private List<RecyclerModel> barcodeModel;
    private Adapter adapter;
    private View itemView;
    private static final String PREF_NAME = "barcodeStorage";
    private static final String KEY_PREFIX = "Data_";
    private TextView safetyTextView, safetyRealText;

    private SharedPreferences sharedPreferences = null;

    private QrListBinding binding;

    public QRList() {
        //Empty Constructor

        //QRList Banner
        //ca-app-pub-1436561055108702/7981503859

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = QrListBinding.inflate(getLayoutInflater(), container, false);

        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, getContext().MODE_PRIVATE);
        barcodeModel = new ArrayList<>();


        // Retrieve the number of items stored previously (if any)

        itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_design,
                requireActivity().findViewById(R.id.itemContainer));



        safetyTextView = itemView.findViewById(R.id.safetyStatusText);


        int itemCount = sharedPreferences.getInt("itemCount", 0);


        Bundle bundle = getArguments();


        if (itemCount > 0) {
            // Load previously saved items
            for (int i = itemCount; 1 <= i; i--) {

                String _type = sharedPreferences.getString(KEY_PREFIX + i + "_Type", "");
                String _raw = sharedPreferences.getString(KEY_PREFIX + i + "_Raw", "");
                String _time = sharedPreferences.getString(KEY_PREFIX + i + "_Time", "");
                String _safety = sharedPreferences.getString(KEY_PREFIX + i + "_Safety", "");
                Log.d("Kayıtlı: ", _type);
                if (_type.matches("URL")) {
                    initData(_type, _raw, _time, _safety);

                } else if (_type.matches("Ürün")) {
                    initData(_type, _raw, _time, "");

                }

            }
        } else {
            barcodeModel.add(new RecyclerModel(R.drawable.link, "URL", "www.google.com", "Güvenli", "unknown", null));
        }


        if (bundle != null) {

            String type = bundle.getString("KEY_TYPE");
            String raw = bundle.getString("KEY_RAW");
            String time = bundle.getString("KEY_TIME");

            Log.d("Tipi: ", "" + type);
            Log.d("Güvenlik: ", "" + QRviaCamera.safety);

            initData(type, raw, time, QRviaCamera.safety);
            if (type.matches("Ürün")) saveData(type, raw, time, "");
            else {
                saveData(type, raw, time, QRviaCamera.safety);
            }
        }

        initRecyclerView();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLongClickable(true);

        safetyRealText = view.findViewById(R.id.safetyStatusText);


    }

    @SuppressLint("SetTextI18n")
    private void initData(final String type, final String raw, final String time, final String safety) {


        switch (type) {

            case "URL": {

                barcodeModel.add(new RecyclerModel(R.drawable.link, type, raw, safety, time, null));


                if(QRviaCamera.safety != null){

                    switch (QRviaCamera.safety) {

                        case "Güvenli":

                            safetyRealText.setTextColor(Color.GREEN);

                            break;
                        case "Belirsiz":

                            safetyRealText.setTextColor(Color.YELLOW);
                            break;
                        case "Tehlikeli":

                            safetyRealText.setTextColor(Color.RED);


                            break;
                        default:

                            break;
                    }

                }
                else{

                    safetyTextView.setText("Taranmadı");
                    safetyTextView.setTextColor(Color.YELLOW);

                }

            }
            break;

            case "Ürün": {

                barcodeModel.add(new RecyclerModel(R.drawable.product, type, raw, "", time, null));

            }
            break;
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {

        manager = new LinearLayoutManager(requireContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new Adapter(barcodeModel, this);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    private void saveData(String type, String raw, String time, String safety) {
        int itemCount = sharedPreferences.getInt("itemCount", 0);
        itemCount++;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PREFIX + itemCount + "_Type", type);
        editor.putString(KEY_PREFIX + itemCount + "_Raw", raw);
        editor.putString(KEY_PREFIX + itemCount + "_Time", time);
        editor.putString(KEY_PREFIX + itemCount + "_Safety", safety);
        editor.putInt("itemCount", itemCount);
        editor.apply();
    }


    @Override
    public void onClickListener(int position) {

       // Toast.makeText(requireContext(), "Tıklandı: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongClickListener(int position) {
        barcodeModel.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(requireContext(), "Kaldırıldı.", Toast.LENGTH_SHORT).show();

    }
}
