package pe.mobytes.examplemvvm1.ui.repo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import java.util.Objects;

import pe.mobytes.examplemvvm1.R;
import pe.mobytes.examplemvvm1.databinding.ContributorItemBinding;
import pe.mobytes.examplemvvm1.model.Contributor;
import pe.mobytes.examplemvvm1.ui.common.DataBoundListAdapter;

public class ContributorAdapter extends DataBoundListAdapter<Contributor, ContributorItemBinding> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private final ContributorClickCallback callback;

    public ContributorAdapter(androidx.databinding.DataBindingComponent dataBindingComponent,
                              ContributorClickCallback callback) {
        this.dataBindingComponent = dataBindingComponent;
        this.callback = callback;
    }

    @Override
    protected ContributorItemBinding createBinding(ViewGroup parent) {
        ContributorItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.contributor_item, parent, false, dataBindingComponent);

        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contributor contributor = binding.getContributor();
                if(contributor != null && callback != null){
                    callback.onClick(contributor);
                }
            }
        });
        return binding;
    }

    @Override
    protected void bind(ContributorItemBinding binding, Contributor item) {
        binding.setContributor(item);
    }

    @Override
    protected boolean areItemsTheSame(Contributor oldItem, Contributor newItem) {
        return Objects.equals(oldItem.getLogin(), newItem.getLogin());
    }

    @Override
    protected boolean areContentsTheSame(Contributor oldItem, Contributor newItem) {
        return Objects.equals(oldItem.getAvatarUrl(), newItem.getAvatarUrl()) &&
                Objects.equals(oldItem.getContributions(), newItem.getContributions());
    }

    public interface ContributorClickCallback{
        void onClick(Contributor contributor);
    }
}
